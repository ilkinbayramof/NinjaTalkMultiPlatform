package com.ilkinbayramov.ninjatalk.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilkinbayramov.ninjatalk.data.dto.Conversation
import com.ilkinbayramov.ninjatalk.data.repository.ChatRepository
import com.ilkinbayramov.ninjatalk.presentation.chat.ChatListViewModel
import com.ilkinbayramov.ninjatalk.ui.theme.*

@Composable
fun ChatListScreen(onConversationClick: (String) -> Unit) {
    val viewModel = remember { ChatListViewModel(ChatRepository()) }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadConversations() }

    Scaffold(containerColor = NinjaBackground) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                    text = "Mesajlar",
                    color = Color.White,
                    style =
                            MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                            ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NinjaPrimary)
                }
            } else if (state.conversations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Henüz mesajınız yok", color = NinjaTextSecondary, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                ) {
                    items(state.conversations) { conversation ->
                        ConversationItem(
                                conversation = conversation,
                                onClick = { onConversationClick(conversation.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationItem(conversation: Conversation, onClick: () -> Unit) {
    Surface(
            shape = RoundedCornerShape(16.dp),
            color = NinjaSurface,
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Anonymous Icon
            Box(
                    modifier = Modifier.size(60.dp).clip(CircleShape).background(NinjaPrimary),
                    contentAlignment = Alignment.Center
            ) {
                Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Anonymous User",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = conversation.otherUserAnonymousName,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                    )

                    if (conversation.unreadCount > 0) {
                        Badge(containerColor = NinjaPrimary) {
                            Text(
                                    text = conversation.unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                        text = conversation.lastMessage ?: "Henüz mesaj yok",
                        color = NinjaTextSecondary,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
