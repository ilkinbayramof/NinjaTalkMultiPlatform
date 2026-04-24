package com.ilkinbayramov.ninjatalk.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.sp
import com.ilkinbayramov.ninjatalk.ui.theme.*
import com.ilkinbayramov.ninjatalk.localization.*
import com.ilkinbayramov.ninjatalk.LocalLanguageController
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onLogout: () -> Unit = {}) {
        var searchQuery by remember { mutableStateOf("") }
        var showProfileEdit by remember { mutableStateOf(false) }
        var showPasswordChange by remember { mutableStateOf(false) }
        var showBlockedUsers by remember { mutableStateOf(false) }
        var showPrivacyPolicy by remember { mutableStateOf(false) }
        var showLanguageDialog by remember { mutableStateOf(false) }
        val strings = LocalAppStrings.current
        val changeLanguage = LocalLanguageController.current

        if (showProfileEdit) {
                com.ilkinbayramov.ninjatalk.ui.settings.profile.ProfileEditScreen(
                        onBackClick = { showProfileEdit = false }
                )
                return
        }

        if (showPasswordChange) {
                com.ilkinbayramov.ninjatalk.ui.settings.password.ChangePasswordScreen(
                        onBackClick = { showPasswordChange = false }
                )
                return
        }

        if (showBlockedUsers) {
                com.ilkinbayramov.ninjatalk.ui.settings.blocked.BlockedUsersScreen(
                        onBackClick = { showBlockedUsers = false }
                )
                return
        }

        if (showPrivacyPolicy) {
                com.ilkinbayramov.ninjatalk.ui.settings.privacy.PrivacyPolicyScreen(
                        onBackClick = { showPrivacyPolicy = false }
                )
                return
        }

        // All settings items with their categories
        val allSettings = remember(strings) {
                listOf(
                        SettingCategory(
                                strings.account,
                                listOf(
                                        SettingItemData(
                                                strings.profileInfo,
                                                strings.profileInfoDesc,
                                                Icons.Default.Person
                                        ),
                                        SettingItemData(strings.changePassword, null, Icons.Default.Lock)
                                )
                        ),
                        SettingCategory(
                                strings.notifications,
                                listOf(
                                        SettingItemData(
                                                strings.notifications,
                                                null,
                                                Icons.Default.Notifications,
                                                isToggle = true
                                        )
                                )
                        ),
                        SettingCategory(
                                strings.privacyPolicy,
                                listOf(
                                        SettingItemData(
                                                strings.blockedUsers,
                                                null,
                                                Icons.Default.Block
                                        ),
                                        SettingItemData(
                                                strings.privacyPolicy,
                                                null,
                                                Icons.Default.Security
                                        )
                                )
                        ),
                        SettingCategory(
                                strings.application,
                                listOf(
                                        SettingItemData(strings.language, null, Icons.Default.Language),
                                )
                        )
                )
        }

        // Filter settings based on search query
        val filteredSettings =
                remember(searchQuery, allSettings) {
                        if (searchQuery.isBlank()) {
                                allSettings
                        } else {
                                allSettings.mapNotNull { category ->
                                        val filteredItems =
                                                category.items.filter {
                                                        it.title.contains(
                                                                searchQuery,
                                                                ignoreCase = true
                                                        )
                                                }
                                        if (filteredItems.isNotEmpty()) {
                                                SettingCategory(category.title, filteredItems)
                                        } else null
                                }
                        }
                }

        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .background(NinjaBackground)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp)
        ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                        text = strings.settingsTitle,
                        color = Color.White,
                        style =
                                MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(strings.searchSettings, color = NinjaTextSecondary) },
                        leadingIcon = {
                                Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = NinjaTextSecondary
                                )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = NinjaSurface,
                                        unfocusedContainerColor = NinjaSurface,
                                        cursorColor = NinjaPrimary,
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Render filtered settings
                var notificationsEnabled by remember { mutableStateOf(true) }
                val scope = rememberCoroutineScope()

                // Load notification setting on start
                LaunchedEffect(Unit) {
                        notificationsEnabled =
                                com.ilkinbayramov.ninjatalk.utils.TokenManager
                                        .getNotificationsEnabled()
                }

                filteredSettings.forEach { category ->
                        SectionTitle(category.title)

                        category.items.forEach { item ->
                                if (item.isToggle) {
                                        SettingsToggleItem(
                                                icon = item.icon,
                                                title = item.title,
                                                isChecked = notificationsEnabled,
                                                onCheckedChange = { enabled ->
                                                        notificationsEnabled = enabled
                                                        scope.launch {
                                                                com.ilkinbayramov.ninjatalk.utils
                                                                        .TokenManager
                                                                        .setNotificationsEnabled(
                                                                                enabled
                                                                        )
                                                        }
                                                }
                                        )
                                } else {
                                        SettingsItem(
                                                icon = item.icon,
                                                title = item.title,
                                                subtitle = item.subtitle,
                                                onClick = {
                                                        when (item.title) {
                                                                strings.profileInfo ->
                                                                        showProfileEdit = true
                                                                strings.changePassword ->
                                                                        showPasswordChange = true
                                                                strings.blockedUsers ->
                                                                        showBlockedUsers = true
                                                                strings.privacyPolicy ->
                                                                        showPrivacyPolicy = true
                                                                strings.language ->
                                                                        showLanguageDialog = true
                                                        }
                                                }
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                }

                if (searchQuery.isBlank()) {
                        // Çıkış Yap Button
                        Button(
                                onClick = onLogout,
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NinjaPrimary)
                        ) { Text(strings.logout, fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Hesabı Sil Button with confirmation
                        var showDeleteDialog by remember { mutableStateOf(false) }
                        val userRepository = remember {
                                com.ilkinbayramov.ninjatalk.data.repository.UserRepository()
                        }

                        if (showDeleteDialog) {
                                AlertDialog(
                                        onDismissRequest = { showDeleteDialog = false },
                                        title = { Text(strings.deleteAccount, color = Color.White) },
                                        text = {
                                                Text(
                                                        strings.deleteAccountConfirm,
                                                        color = Color.White
                                                )
                                        },
                                        confirmButton = {
                                                TextButton(
                                                        onClick = {
                                                                showDeleteDialog = false
                                                                scope.launch {
                                                                        userRepository
                                                                                .deleteAccount()
                                                                                .onSuccess {
                                                                                        // Clear
                                                                                        // token and
                                                                                        // logout
                                                                                        com.ilkinbayramov
                                                                                                .ninjatalk
                                                                                                .utils
                                                                                                .TokenManager
                                                                                                .clearToken()
                                                                                        onLogout()
                                                                                }
                                                                                .onFailure { error
                                                                                        ->
                                                                                        // Handle
                                                                                        // error
                                                                                        // silently
                                                                                        // or show
                                                                                        // message
                                                                                        println(
                                                                                                "Delete account error: ${error.message}"
                                                                                        )
                                                                                }
                                                                }
                                                        }
                                                ) {
                                                        Text(
                                                                strings.delete,
                                                                color = Color.Red,
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                }
                                        },
                                        dismissButton = {
                                                TextButton(onClick = { showDeleteDialog = false }) {
                                                        Text(strings.cancel, color = Color.White)
                                                }
                                        },
                                        containerColor = NinjaSurface
                                )
                        }

                        TextButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.fillMaxWidth()
                        ) { Text(strings.deleteAccount, color = Color.Red, fontWeight = FontWeight.Medium) }
                }

                Spacer(modifier = Modifier.height(24.dp))
        }

        if (showLanguageDialog) {
                AlertDialog(
                        onDismissRequest = { showLanguageDialog = false },
                        containerColor = NinjaSurface,
                        title = { Text(strings.language, color = Color.White) },
                        text = {
                                Column {
                                        AppLanguage.entries.forEach { lang ->
                                                Text(
                                                        text = lang.displayName,
                                                        color = Color.White,
                                                        modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                        changeLanguage(lang)
                                                                        showLanguageDialog = false
                                                                }
                                                                .padding(vertical = 12.dp)
                                                )
                                        }
                                }
                        },
                        confirmButton = {
                                TextButton(onClick = { showLanguageDialog = false }) {
                                        Text(strings.cancel, color = Color.White)
                                }
                        }
                )
        }
}

// Data classes for settings
data class SettingCategory(val title: String, val items: List<SettingItemData>)

data class SettingItemData(
        val title: String,
        val subtitle: String?,
        val icon: ImageVector,
        val isToggle: Boolean = false
)

@Composable
private fun SectionTitle(title: String) {
        Text(
                text = title,
                color = NinjaTextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
        )
}

@Composable
private fun SettingsItem(
        icon: ImageVector,
        title: String,
        subtitle: String? = null,
        onClick: () -> Unit
) {
        Surface(
                shape = RoundedCornerShape(12.dp),
                color = NinjaSurface,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
                Row(
                        modifier = Modifier.clickable(onClick = onClick).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Box(
                                modifier =
                                        Modifier.size(40.dp)
                                                .clip(CircleShape)
                                                .background(NinjaBackground),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = title,
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                )
                                if (subtitle != null) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                                text = subtitle,
                                                color = NinjaTextSecondary,
                                                fontSize = 14.sp
                                        )
                                }
                        }

                        Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = NinjaTextSecondary
                        )
                }
        }
}

@Composable
private fun SettingsToggleItem(
        icon: ImageVector,
        title: String,
        isChecked: Boolean,
        onCheckedChange: (Boolean) -> Unit
) {
        Surface(
                shape = RoundedCornerShape(12.dp),
                color = NinjaSurface,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Box(
                                modifier =
                                        Modifier.size(40.dp)
                                                .clip(CircleShape)
                                                .background(NinjaBackground),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                                text = title,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                        )

                        Switch(
                                checked = isChecked,
                                onCheckedChange = onCheckedChange,
                                colors =
                                        SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = NinjaPrimary,
                                                uncheckedThumbColor = Color.White,
                                                uncheckedTrackColor = Color.Gray
                                        )
                        )
                }
        }
}
