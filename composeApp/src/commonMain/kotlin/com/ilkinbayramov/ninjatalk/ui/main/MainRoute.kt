package com.ilkinbayramov.ninjatalk.ui.main

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
import com.ilkinbayramov.ninjatalk.data.repository.ChatRepository
import com.ilkinbayramov.ninjatalk.ui.chat.ChatListScreen
import com.ilkinbayramov.ninjatalk.ui.chat.InboxScreen
import com.ilkinbayramov.ninjatalk.ui.settings.SettingsScreen
import com.ilkinbayramov.ninjatalk.ui.shuffle.ShuffleScreen
import com.ilkinbayramov.ninjatalk.ui.theme.*
import com.ilkinbayramov.ninjatalk.utils.TokenManager
import kotlinx.coroutines.launch

sealed class MainTab(val route: String, val label: String, val icon: ImageVector) {
    object Chat : MainTab("chat", "Sohbet", Icons.Default.Chat)
    object Shuffle : MainTab("shuffle", "Keşfet", Icons.Default.Shuffle)
    object Premium : MainTab("premium", "Premium", Icons.Default.Star)
    object Profile : MainTab("profile", "Profil", Icons.Default.Person)
}

@Composable
fun MainRoute(onLogout: () -> Unit = {}) {
    var selectedTab by remember { mutableStateOf<MainTab>(MainTab.Shuffle) }
    var currentConversationId by remember { mutableStateOf<String?>(null) }
    var currentConversationName by remember { mutableStateOf("Anonim Sohbet") }
    var currentOtherUserId by remember { mutableStateOf<String?>(null) }
    var unreadCount by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val chatRepository = remember { ChatRepository() }

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
                                                                                ?: "Anonim Sohbet"
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
                                        onUserClick = { user ->
                                            // Create or get conversation and navigate to inbox
                                            // Use real name since user is initiating the chat
                                            val realName =
                                                    user.email.substringBefore("@")
                                                            .replaceFirstChar { it.uppercase() }
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
                                        }
                                )
                        MainTab.Premium -> PlaceholderTab("Premium")
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
                    contentDescription = tab.label,
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
                text = tab.label,
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

@Composable
private fun PlaceholderTab(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
                text = "$title\n(Yakında)",
                color = NinjaTextSecondary,
                style = MaterialTheme.typography.bodyLarge
        )
    }
}
