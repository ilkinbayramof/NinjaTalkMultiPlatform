#!/usr/bin/env python3
"""
Xcode project.pbxproj faylına Firebase (FirebaseCore, FirebaseMessaging)
dependency-lərini əlavə edir.
"""

import re

PBXPROJ_PATH = "iosApp/iosApp.xcodeproj/project.pbxproj"

# Existing IDs from the project
PACKAGE_REF_ID = "20DDCDAD2F0E93310017515D"  # firebase-ios-sdk package reference
FRAMEWORKS_BUILD_PHASE_ID = "B22309701E6C8B837AC0A15E"

# New IDs we generate (fixed, so script is idempotent)
FIREBASE_CORE_PRODUCT_ID    = "AA01BB02CC03DD04EE05FF06"
FIREBASE_MESSAGING_PRODUCT_ID = "AA01BB02CC03DD04EE05FF07"
FIREBASE_CORE_BUILDFILE_ID  = "AA01BB02CC03DD04EE05FF08"
FIREBASE_MESSAGING_BUILDFILE_ID = "AA01BB02CC03DD04EE05FF09"

with open(PBXPROJ_PATH, "r") as f:
    content = f.read()

# --- Guard: already patched? ---
if FIREBASE_CORE_PRODUCT_ID in content:
    print("✅ Firebase dependencies already present in project.pbxproj")
    exit(0)

# -----------------------------------------------------------------------
# 1. PBXBuildFile section - add build file entries
# -----------------------------------------------------------------------
build_file_entries = (
    f"\t\t{FIREBASE_CORE_BUILDFILE_ID} /* FirebaseCore in Frameworks */ = "
    f"{{isa = PBXBuildFile; productRef = {FIREBASE_CORE_PRODUCT_ID} /* FirebaseCore */; }};\n"
    f"\t\t{FIREBASE_MESSAGING_BUILDFILE_ID} /* FirebaseMessaging in Frameworks */ = "
    f"{{isa = PBXBuildFile; productRef = {FIREBASE_MESSAGING_PRODUCT_ID} /* FirebaseMessaging */; }};\n"
)

content = content.replace(
    "/* Begin PBXFileReference section */",
    build_file_entries + "/* Begin PBXFileReference section */"
)

# -----------------------------------------------------------------------
# 2. PBXFrameworksBuildPhase - add files to Frameworks build phase
# -----------------------------------------------------------------------
frameworks_files = (
    f"\t\t\t\t{FIREBASE_CORE_BUILDFILE_ID} /* FirebaseCore in Frameworks */,\n"
    f"\t\t\t\t{FIREBASE_MESSAGING_BUILDFILE_ID} /* FirebaseMessaging in Frameworks */,\n"
)

# Find the Frameworks build phase and add files inside its files = ( ... )
pattern = r'(B22309701E6C8B837AC0A15E /\* Frameworks \*/ = \{[^}]*files = \()'
replacement = r'\1\n' + frameworks_files.rstrip('\n')
content = re.sub(pattern, replacement, content, flags=re.DOTALL)

# -----------------------------------------------------------------------
# 3. Target packageProductDependencies - link the products to the target
# -----------------------------------------------------------------------
product_deps = (
    f"\t\t\t\t{FIREBASE_CORE_PRODUCT_ID} /* FirebaseCore */,\n"
    f"\t\t\t\t{FIREBASE_MESSAGING_PRODUCT_ID} /* FirebaseMessaging */,\n"
)

content = content.replace(
    "\t\t\t\tpackageProductDependencies = (\n\t\t\t\t);",
    f"\t\t\t\tpackageProductDependencies = (\n{product_deps}\t\t\t\t);"
)

# -----------------------------------------------------------------------
# 4. XCSwiftPackageProductDependency section - define the products
# -----------------------------------------------------------------------
spm_product_section = (
    "\n/* Begin XCSwiftPackageProductDependency section */\n"
    f"\t\t{FIREBASE_CORE_PRODUCT_ID} /* FirebaseCore */ = {{"
    f"isa = XCSwiftPackageProductDependency; "
    f"package = {PACKAGE_REF_ID} /* XCRemoteSwiftPackageReference \"firebase-ios-sdk\" */; "
    f"productName = FirebaseCore; }};\n"
    f"\t\t{FIREBASE_MESSAGING_PRODUCT_ID} /* FirebaseMessaging */ = {{"
    f"isa = XCSwiftPackageProductDependency; "
    f"package = {PACKAGE_REF_ID} /* XCRemoteSwiftPackageReference \"firebase-ios-sdk\" */; "
    f"productName = FirebaseMessaging; }};\n"
    "/* End XCSwiftPackageProductDependency section */\n"
)

content = content.replace(
    "/* Begin XCRemoteSwiftPackageReference section */",
    spm_product_section + "/* Begin XCRemoteSwiftPackageReference section */"
)

# -----------------------------------------------------------------------
# Write back
# -----------------------------------------------------------------------
with open(PBXPROJ_PATH, "w") as f:
    f.write(content)

print("✅ Firebase dependencies successfully added to project.pbxproj!")
print("   → FirebaseCore")
print("   → FirebaseMessaging")
print("\n📌 Next steps:")
print("   1. Xcode-da: Product → Clean Build Folder (⇧⌘K)")
print("   2. Xcode-da: File → Packages → Resolve Package Versions")
print("   3. Run edin (▶)")
