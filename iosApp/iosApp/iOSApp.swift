import SwiftUI
import FirebaseCore
import FirebaseMessaging
import UserNotifications

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate, MessagingDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        // Initialize Firebase
        FirebaseApp.configure()
        Messaging.messaging().delegate = self
        
        // Request notification permissions
        UNUserNotificationCenter.current().delegate = self
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: { granted, error in
                if granted {
                    print("✅ iOS: Notification permission granted")
                } else {
                    print("❌ iOS: Notification permission denied - \(error?.localizedDescription ?? "unknown")")
                }
            }
        )
        
        application.registerForRemoteNotifications()
        print("📱 iOS: App initialized with Firebase")
        return true
    }
    
    // FCM Token received
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("🔥 iOS FCM: Token received")
        if let token = fcmToken {
            print("🔥 iOS FCM: Token - \(token.prefix(20))...")
            FcmTokenBridge.shared.saveToken(token: token)
        } else {
            print("⚠️ iOS FCM: Token is nil")
        }
    }
    
    // APNs Token received
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        print("📱 iOS: APNs device token received")
        Messaging.messaging().apnsToken = deviceToken
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("❌ iOS: Failed to register for remote notifications - \(error.localizedDescription)")
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate {
    // Handle notification when app is in foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        print("🔔 iOS: Notification received in foreground")
        completionHandler([[.banner, .sound, .badge]])
    }
    
    // Handle notification tap
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        print("👆 iOS: Notification tapped")
        let userInfo = response.notification.request.content.userInfo
        print("📨 iOS: Notification data - \(userInfo)")
        completionHandler()
    }
}