package com.ilkinbayramov.ninjatalk.ui.settings.blocked

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilkinbayramov.ninjatalk.data.dto.User
import com.ilkinbayramov.ninjatalk.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedUsersScreen(onBackClick: () -> Unit) {
    val userRepository = remember { com.ilkinbayramov.ninjatalk.data.repository.UserRepository() }
    var blockedUsers by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Load blocked users on start
    LaunchedEffect(Unit) {
        println("DEBUG: Loading blocked users...")
        userRepository
                .getBlockedUsers()
                .onSuccess { users ->
                    println("DEBUG: Blocked users received: ${users.size} users")
                    users.forEach { user ->
                        println("DEBUG: Blocked user - id: ${user.id}, email: ${user.email}")
                    }
                    blockedUsers = users
                    isLoading = false
                }
                .onFailure { error ->
                    println("ERROR: Failed to load blocked users: ${error.message}")
                    error.printStackTrace()
                    isLoading = false
                }
    }

    Scaffold(
            containerColor = NinjaBackground,
            topBar = {
                TopAppBar(
                        title = { Text("Engellenen Kullanıcılar", color = Color.White) },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = NinjaSurface)
                )
            }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = NinjaPrimary
                    )
                }
                blockedUsers.isEmpty() -> {
                    Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                                "Engellenmiş kullanıcı yok",
                                color = NinjaTextSecondary,
                                fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(blockedUsers) { user ->
                            BlockedUserItem(
                                    user = user,
                                    onUnblock = {
                                        scope.launch {
                                            userRepository.unblockUser(user.id).onSuccess {
                                                // Remove from list
                                                blockedUsers =
                                                        blockedUsers.filter { it.id != user.id }
                                            }
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BlockedUserItem(user: User, onUnblock: () -> Unit) {
    Surface(
            shape = RoundedCornerShape(12.dp),
            color = NinjaSurface,
            modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Profile placeholder
            Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(NinjaBackground),
                    contentAlignment = Alignment.Center
            ) {
                Text(
                        text = user.email.first().uppercase(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = user.email.substringBefore("@"),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                )
                if (user.bio != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                            text = user.bio,
                            color = NinjaTextSecondary,
                            fontSize = 14.sp,
                            maxLines = 1
                    )
                }
            }

            TextButton(onClick = onUnblock) {
                Text("Engeli Kaldır", color = NinjaPrimary, fontSize = 14.sp)
            }
        }
    }
}
