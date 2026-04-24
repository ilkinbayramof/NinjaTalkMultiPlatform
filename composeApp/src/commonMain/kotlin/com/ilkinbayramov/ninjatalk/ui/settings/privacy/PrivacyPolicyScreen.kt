package com.ilkinbayramov.ninjatalk.ui.settings.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilkinbayramov.ninjatalk.ui.theme.NinjaBackground
import com.ilkinbayramov.ninjatalk.ui.theme.NinjaPrimary
import com.ilkinbayramov.ninjatalk.ui.theme.NinjaSurface
import com.ilkinbayramov.ninjatalk.ui.theme.NinjaTextSecondary
import com.ilkinbayramov.ninjatalk.localization.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBackClick: () -> Unit) {
    val strings = LocalAppStrings.current
    Scaffold(
        containerColor = NinjaBackground,
        topBar = {
            TopAppBar(
                title = { Text(strings.privacyPolicy, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.back,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NinjaSurface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = strings.lastUpdate,
                color = NinjaTextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            PolicySection(
                title = strings.privacyDataCollectedTitle,
                content = strings.privacyDataCollectedContent
            )

            PolicySection(
                title = strings.privacyDataUseTitle,
                content = strings.privacyDataUseContent
            )

            PolicySection(
                title = strings.privacyDataSecurityTitle,
                content = strings.privacyDataSecurityContent
            )

            PolicySection(
                title = strings.privacyAccountDeletionTitle,
                content = strings.privacyAccountDeletionContent
            )

            PolicySection(
                title = strings.privacyPermissionsTitle,
                content = strings.privacyPermissionsContent
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = title,
            color = NinjaPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            color = Color.White,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
    }
}
