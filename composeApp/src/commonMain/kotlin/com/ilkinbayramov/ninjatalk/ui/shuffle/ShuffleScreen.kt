package com.ilkinbayramov.ninjatalk.ui.shuffle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilkinbayramov.ninjatalk.ui.shuffle.filter.ShuffleFilterBottomSheet
import com.ilkinbayramov.ninjatalk.ui.theme.*

data class MockUser(val name: String, val age: Int, val gender: String, val bio: String)

@Composable
fun ShuffleScreen() {
        var searchQuery by remember { mutableStateOf("") }
        var showFilterSheet by remember { mutableStateOf(false) }

        // Mock users
        val mockUsers = remember {
                listOf(
                        MockUser(
                                "Ayşe",
                                26,
                                "Kadın",
                                "Seyahat etmeyi ve yeni insanlarla tanışmayı seviyorum 🌍"
                        ),
                        MockUser(
                                "Mehmet",
                                29,
                                "Erkek",
                                "Müzik dinlemeyi ve spor yapmayı seviyorum 🎵⚽"
                        ),
                        MockUser(
                                "Zeynep",
                                24,
                                "Kadın",
                                "Kitap okumayı ve kahve içmeyi seviyorum ☕📚"
                        )
                )
        }

        if (showFilterSheet) {
                ShuffleFilterBottomSheet(
                        onDismiss = { showFilterSheet = false },
                        onApplyFilters = { filters ->
                                // TODO: Apply filters
                                showFilterSheet = false
                        }
                )
        }

        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .background(NinjaBackground)
                                .padding(horizontal = 16.dp)
        ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                        text = "Keşfet",
                        color = Color.White,
                        style =
                                MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Search TextField
                OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Kullanıcı adı ara…", color = NinjaTextSecondary) },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = NinjaSurface,
                                        unfocusedContainerColor = NinjaSurface,
                                        cursorColor = NinjaPrimary,
                                        focusedBorderColor = NinjaPrimary,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Button
                Surface(
                        shape = RoundedCornerShape(50),
                        color = NinjaSurface,
                        modifier = Modifier.height(44.dp).wrapContentWidth(),
                        onClick = { showFilterSheet = true }
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 18.dp)
                        ) {
                                Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = null,
                                        tint = NinjaTextSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Filtrele", color = Color.White)
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User List
                LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                ) { items(mockUsers) { user -> UserCard(user) } }
        }
}

@Composable
private fun UserCard(user: MockUser) {
        Surface(
                shape = RoundedCornerShape(16.dp),
                color = NinjaSurface,
                modifier = Modifier.fillMaxWidth()
        ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        // Avatar
                        Box(
                                modifier =
                                        Modifier.size(60.dp)
                                                .clip(CircleShape)
                                                .background(NinjaPrimary),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = user.name.first().toString(),
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Info
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = user.name,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                        text = user.bio,
                                        color = NinjaTextSecondary,
                                        fontSize = 14.sp,
                                        maxLines = 2
                                )
                        }
                }
        }
}
