package com.ilkinbayramov.ninjatalk.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilkinbayramov.ninjatalk.data.dto.Message
import com.ilkinbayramov.ninjatalk.data.repository.ChatRepository
import com.ilkinbayramov.ninjatalk.presentation.chat.InboxViewModel
import com.ilkinbayramov.ninjatalk.ui.theme.*
import com.ilkinbayramov.ninjatalk.utils.TokenManager
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
        conversationId: String,
        otherUserId: String,
        conversationName: String = "Anonim Sohbet",
        onBackClick: () -> Unit
) {
    val viewModel = remember { InboxViewModel(conversationId, ChatRepository()) }
    val state by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var isUserBlocked by remember { mutableStateOf(false) }
    var currentUserId by remember { mutableStateOf<String?>(null) }
    val userRepository = remember { com.ilkinbayramov.ninjatalk.data.repository.UserRepository() }

    // Load userId on screen load
    LaunchedEffect(Unit) { currentUserId = TokenManager.getUserId() }

    // Check if user is blocked on screen load
    LaunchedEffect(otherUserId) {
        userRepository.getBlockedUsers().onSuccess { blockedUsers ->
            isUserBlocked = blockedUsers.any { it.id == otherUserId }
            println("DEBUG: Is user $otherUserId blocked? $isUserBlocked")
        }
    }

    // Block confirmation dialog
    if (showBlockDialog) {
        AlertDialog(
                onDismissRequest = { showBlockDialog = false },
                title = { Text("Kullanıcıyı Engelle", color = Color.White) },
                text = {
                    Text(
                            "Bu kullanıcıyı engellemek istediğinizden emin misiniz? " +
                                    "Engelledikten sonra bu kullanıcıdan mesaj alamayacaksınız.",
                            color = Color.White
                    )
                },
                confirmButton = {
                    TextButton(
                            onClick = {
                                scope.launch {
                                    println("DEBUG: Block button clicked for user: $otherUserId")
                                    userRepository
                                            .blockUser(otherUserId)
                                            .onSuccess {
                                                println(
                                                        "DEBUG: Block successful for user: $otherUserId"
                                                )
                                                isUserBlocked = true
                                                showBlockDialog = false
                                            }
                                            .onFailure { error ->
                                                println("ERROR: Block failed: ${error.message}")
                                                error.printStackTrace()
                                                showBlockDialog = false
                                            }
                                }
                            }
                    ) { Text("Engelle", color = Color.Red, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showBlockDialog = false }) {
                        Text("İptal", color = Color.White)
                    }
                },
                containerColor = NinjaSurface
        )
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(state.messages.size - 1) }
        }
    }

    if (showBottomSheet) {
        ChatOptionsBottomSheet(
                onDismiss = { showBottomSheet = false },
                onBlock = {
                    showBottomSheet = false
                    showBlockDialog = true
                },
                onClearChat = {
                    showBottomSheet = false
                    viewModel.clearMessages()
                }
        )
    }

    Scaffold(
            containerColor = NinjaBackground,
            topBar = {
                TopAppBar(
                        title = {
                            Text(
                                    text = conversationName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { showBottomSheet = true }) {
                                Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Options",
                                        tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = NinjaSurface)
                )
            },
            bottomBar = {
                Surface(color = NinjaSurface, shadowElevation = 8.dp) {
                    if (isUserBlocked) {
                        // Show unblock button when user is blocked
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                    onClick = {
                                        scope.launch {
                                            println(
                                                    "DEBUG: Unblock button clicked for user: $otherUserId"
                                            )
                                            userRepository
                                                    .unblockUser(otherUserId)
                                                    .onSuccess {
                                                        println("DEBUG: Unblock successful")
                                                        isUserBlocked = false
                                                    }
                                                    .onFailure { error ->
                                                        println(
                                                                "ERROR: Unblock failed: ${error.message}"
                                                        )
                                                    }
                                        }
                                    },
                                    colors =
                                            ButtonDefaults.buttonColors(
                                                    containerColor = NinjaPrimary
                                            ),
                                    shape = RoundedCornerShape(24.dp)
                            ) { Text("Engeli Kaldır", color = Color.White) }
                        }
                    } else {
                        // Normal message input
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                    value = messageText,
                                    onValueChange = { messageText = it },
                                    placeholder = {
                                        Text("Mesajınızı yazın...", color = NinjaTextSecondary)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(24.dp),
                                    colors =
                                            OutlinedTextFieldDefaults.colors(
                                                    focusedContainerColor = NinjaBackground,
                                                    unfocusedContainerColor = NinjaBackground,
                                                    cursorColor = NinjaPrimary,
                                                    focusedBorderColor = NinjaPrimary,
                                                    unfocusedBorderColor = Color.Transparent,
                                                    focusedTextColor = Color.White,
                                                    unfocusedTextColor = Color.White
                                            )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Emoji button
                            IconButton(onClick = { /* TODO: Show emoji picker */}) {
                                Icon(
                                        imageVector = Icons.Default.EmojiEmotions,
                                        contentDescription = "Emoji",
                                        tint = NinjaTextSecondary
                                )
                            }

                            // Send button
                            IconButton(
                                    onClick = {
                                        if (messageText.isNotBlank()) {
                                            viewModel.sendMessage(messageText)
                                            messageText = ""
                                        }
                                    },
                                    enabled = messageText.isNotBlank() && !state.isSending
                            ) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send",
                                        tint =
                                                if (messageText.isNotBlank()) NinjaPrimary
                                                else NinjaTextSecondary
                                )
                            }
                        }
                    }
                }
            }
    ) { padding ->
        if (state.isLoading) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = NinjaPrimary) }
        } else {
            LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.messages) { message ->
                    MessageBubble(
                            message = message,
                            isOwnMessage = message.senderId == currentUserId
                    )
                }
            }
        }
    }
}

@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
private fun MessageBubble(message: Message, isOwnMessage: Boolean) {
    // Format timestamp using kotlinx-datetime
    val instant = Instant.fromEpochMilliseconds(message.timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val timeText =
            "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"

    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        Surface(
                shape =
                        RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isOwnMessage) 16.dp else 4.dp,
                                bottomEnd = if (isOwnMessage) 4.dp else 16.dp
                        ),
                color = if (isOwnMessage) NinjaPrimary else NinjaSurface,
                modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Bottom) {
                Text(
                        text = message.content,
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                        text = timeText,
                        color =
                                if (isOwnMessage) Color.White.copy(alpha = 0.7f)
                                else NinjaTextSecondary,
                        fontSize = 11.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatOptionsBottomSheet(
        onDismiss: () -> Unit,
        onBlock: () -> Unit,
        onClearChat: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = NinjaSurface) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
            // Block option
            TextButton(onClick = onBlock, modifier = Modifier.fillMaxWidth()) {
                Text("Engelle", color = Color.Red, fontSize = 16.sp)
            }

            // Clear chat option
            TextButton(onClick = onClearChat, modifier = Modifier.fillMaxWidth()) {
                Text("Sohbeti Temizle", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
