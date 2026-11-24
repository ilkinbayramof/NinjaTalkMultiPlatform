package com.ilkinbayramov.psst.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilkinbayramov.psst.data.repository.ChatRepository
import com.ilkinbayramov.psst.ui.chat.ChatListScreen
import com.ilkinbayramov.psst.ui.chat.InboxScreen
import com.ilkinbayramov.psst.ui.premium.PremiumScreen
import com.ilkinbayramov.psst.ui.settings.SettingsScreen
import com.ilkinbayramov.psst.ui.shuffle.ShuffleScreen
import com.ilkinbayramov.psst.ui.theme.*
import com.ilkinbayramov.psst.localization.LocalAppStrings
import com.ilkinbayramov.psst.localization.AppStrings
import com.ilkinbayramov.psst.utils.TokenManager
import kotlinx.coroutines.launch

sealed class MainTab(val route: String, val label: (AppStrings) -> String, val icon: ImageVector) {
    object Chat : MainTab("chat", { it.chat }, Icons.Default.Chat)
    object Shuffle : MainTab("shuffle", { it.shuffle }, Icons.Default.Shuffle)
    object Premium : MainTab("premium", { it.premium }, Icons.Default.RocketLaunch)
    object Profile : MainTab("profile", { it.profile }, Icons.Default.Person)
}

@Composable
fun MainRoute(onLogout: () -> Unit = {}) {
    val strings = LocalAppStrings.current
    var selectedTab by remember { mutableStateOf<MainTab>(MainTab.Shuffle) }
    var currentConversationId by remember { mutableStateOf<String?>(null) }
    var currentConversationName by remember { mutableStateOf(strings.anonymousChat) }
    var currentOtherUserId by remember { mutableStateOf<String?>(null) }
    var unreadCount by remember { mutableIntStateOf(0) }
    var currentUser by remember { mutableStateOf<com.ilkinbayramov.psst.data.dto.User?>(null) }
    val scope = rememberCoroutineScope()
    val chatRepository = remember { ChatRepository() }
    val userRepository = remember { com.ilkinbayramov.psst.data.repository.UserRepository() }
    val notificationManager = remember {
        com.ilkinbayramov.psst.notification.createNotificationManager()
    }
    val webSocketManager = remember {
        com.ilkinbayramov.psst.websocket.WebSocketManager(
                com.ilkinbayramov.psst.utils.AppConfig.BASE_URL
        )
    }

    // Function to refresh current user
    fun refreshCurrentUser() {
        scope.launch {
            val token = TokenManager.getToken()
            if (token != null) {
                userRepository.getMe(token).onSuccess { user ->
                    currentUser = user
                    println(
                            "✅ MAIN: User refreshed - email=${user.email}, isPremium=${user.isPremium}"
                    )
                    
                    // Send FCM token to backend after user is loaded
                    try {
                        com.ilkinbayramov.psst.utils.PlatformFcmTokenManager.sendTokenToBackend()
                    } catch (e: Exception) {
                        println("❌ MAIN: FCM token send failed - ${e.message}")
                    }
                }
            }
        }
    }

    // Load current user on init
    LaunchedEffect(Unit) { refreshCurrentUser() }

    // Connect to WebSocket globally
    LaunchedEffect(Unit) {
        val token = TokenManager.getToken()
        if (token != null) {
            println("🌐 MAIN: Connecting to WebSocket...")
            webSocketManager.connect(token)
        }
    }

    // Listen to WebSocket messages globally for notifications
    LaunchedEffect(Unit) {
        webSocketManager.messages.collect { wsMessage ->
            println("📨 MAIN WS: Received message type=${wsMessage.type}")

            if (wsMessage.type == "new_message") {
                wsMessage.message?.let { newMessage ->
                    println(
                            "📬 MAIN: New message - conversationId=${newMessage.conversationId}, current=${currentConversationId}"
                    )

                    // FCM already handles notifications, no need to show here
                    // Just log the message arrival
                    if (newMessage.conversationId != currentConversationId) {
                        println("📨 MAIN: Message from different conversation (FCM will notify)")
                    } else {
                        println("💬 MAIN: Message in current conversation (no notification needed)")
                    }
                }
            }
        }
    }

    // Check for unread messages periodically
    LaunchedEffect(Unit) {
        while (true) {
            val token = TokenManager.getToken()
            if (token != null) {
                chatRepository.getConversations(token).onSuccess { conversations ->
                    unreadCount = conversations.sumOf { it.unreadCount }
                }
            }
            kotlinx.coroutines.delay(5000) // Check every 5 seconds
        }
    }

    Scaffold(
            containerColor = NinjaBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0), // Remove system bar padding
            bottomBar = {
                // Hide bottom bar when in inbox
                if (currentConversationId == null) {
                    BottomBar(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            unreadCount = unreadCount
                    )
                }
            }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                currentConversationId != null -> {
                    InboxScreen(
                            conversationId = currentConversationId!!,
                            otherUserId = currentOtherUserId!!,
                            conversationName = currentConversationName,
                            onBackClick = {
                                currentConversationId = null
                                currentOtherUserId = null
                            }
                    )
                }
                else -> {
                    when (selectedTab) {
                        MainTab.Chat ->
                                ChatListScreen(
                                        onConversationClick = { conversationId ->
                                            // Get conversation from list to get name and
                                            // otherUserId
                                            scope.launch {
                                                val token = TokenManager.getToken()
                                                if (token != null) {
                                                    chatRepository.getConversations(token)
                                                            .onSuccess { conversations ->
                                                                val conversation =
                                                                        conversations.find {
                                                                            it.id == conversationId
                                                                        }
                                                                currentConversationName =
                                                                        conversation
                                                                                ?.otherUserAnonymousName
                                                                                ?: strings.anonymousChat
                                                                currentOtherUserId =
                                                                        conversation?.otherUserId
                                                                currentConversationId =
                                                                        conversationId
                                                            }
                                                }
                                            }
                                        }
                                )
                        MainTab.Shuffle ->
                                ShuffleScreen(
                                        currentUser = currentUser,
                                        onUserClick = { user ->
                                            // Create or get conversation and navigate to inbox
                                            // Use the chosen name since the user is initiating
                                            val realName = user.displayName
                                            scope.launch {
                                                val token = TokenManager.getToken()
                                                if (token != null) {
                                                    chatRepository
                                                            .createConversation(user.id, token)
                                                            .onSuccess { conversationId ->
                                                                currentConversationName = realName
                                                                currentOtherUserId = user.id
                                                                currentConversationId =
                                                                        conversationId
                                                            }
                                                            .onFailure { error ->
                                                                println(
                                                                        "Error creating conversation: ${error.message}"
                                                                )
                                                            }
                                                }
                                            }
                                        },
                                        onNavigateToPremium = { selectedTab = MainTab.Premium }
                                )
                        MainTab.Premium -> PremiumScreen()
                        MainTab.Profile -> SettingsScreen(onLogout = onLogout)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
        selectedTab: MainTab,
        onTabSelected: (MainTab) -> Unit,
        unreadCount: Int = 0
) {
    Box(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(NinjaBackground)
                            .height(58.dp)
                            .padding(horizontal = 16.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            BottomItem(
                    tab = MainTab.Chat,
                    isSelected = selectedTab == MainTab.Chat,
                    onClick = { onTabSelected(MainTab.Chat) },
                    badgeCount = unreadCount
            )

            BottomItem(
                    tab = MainTab.Shuffle,
                    isSelected = selectedTab == MainTab.Shuffle,
                    onClick = { onTabSelected(MainTab.Shuffle) }
            )

            BottomItem(
                    tab = MainTab.Premium,
                    isSelected = selectedTab == MainTab.Premium,
                    onClick = { onTabSelected(MainTab.Premium) }
            )

            BottomItem(
                    tab = MainTab.Profile,
                    isSelected = selectedTab == MainTab.Profile,
                    onClick = { onTabSelected(MainTab.Profile) }
            )
        }
    }
}

@Composable
private fun BottomItem(
        tab: MainTab,
        isSelected: Boolean,
        onClick: () -> Unit,
        badgeCount: Int = 0
) {
    Column(
            modifier = Modifier.width(70.dp).clickable(onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.label(LocalAppStrings.current),
                    tint = if (isSelected) Color.White else Color(0xFF7A7A7A),
                    modifier = Modifier.size(22.dp)
            )

            // Red badge for unread count
            if (badgeCount > 0) {
                Box(
                        modifier =
                                Modifier.align(Alignment.TopEnd)
                                        .offset(x = 6.dp, y = (-4).dp)
                                        .size(16.dp)
                                        .background(Color.Red, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                ) {
                    Text(
                            text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
                text = tab.label(LocalAppStrings.current),
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White else Color(0xFF7A7A7A)
        )

        Spacer(modifier = Modifier.height(2.dp))

        if (isSelected) {
            Box(
                    modifier =
                            Modifier.width(20.dp)
                                    .height(3.dp)
                                    .background(NinjaPrimary, RoundedCornerShape(2.dp))
            )
        }
    }
}
