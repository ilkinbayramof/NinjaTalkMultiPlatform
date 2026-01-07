import Foundation

@objc public class FcmTokenBridge: NSObject {
    @objc public static let shared = FcmTokenBridge()
    private var currentToken: String?
    
    @objc public func saveToken(token: String) {
        currentToken = token
        UserDefaults.standard.set(token, forKey: "fcm_token")
        print("✅ iOS FCM: Token saved - \(token.prefix(20))...")
    }
    
    @objc public func getToken() -> String? {
        if let token = currentToken {
            return token
        }
        return UserDefaults.standard.string(forKey: "fcm_token")
    }
}
