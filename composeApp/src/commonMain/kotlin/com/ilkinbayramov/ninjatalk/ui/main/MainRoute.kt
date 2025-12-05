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
import androidx.compose.ui.unit.dp
import com.ilkinbayramov.ninjatalk.ui.shuffle.ShuffleScreen
import com.ilkinbayramov.ninjatalk.ui.theme.*

sealed class MainTab(val route: String, val label: String, val icon: ImageVector) {
    object Chat : MainTab("chat", "Sohbet", Icons.Default.Chat)
    object Shuffle : MainTab("shuffle", "Keşfet", Icons.Default.Shuffle)
    object Premium : MainTab("premium", "Premium", Icons.Default.Star)
    object Profile : MainTab("profile", "Profil", Icons.Default.Person)
}

@Composable
fun MainRoute() {
    var selectedTab by remember { mutableStateOf<MainTab>(MainTab.Shuffle) }

    Scaffold(
            containerColor = NinjaBackground,
            bottomBar = {
                BottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                MainTab.Chat -> PlaceholderTab("Sohbet")
                MainTab.Shuffle -> ShuffleScreen()
                MainTab.Premium -> PlaceholderTab("Premium")
                MainTab.Profile -> PlaceholderTab("Profil")
            }
        }
    }
}

@Composable
private fun BottomBar(selectedTab: MainTab, onTabSelected: (MainTab) -> Unit) {
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
                    onClick = { onTabSelected(MainTab.Chat) }
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
private fun BottomItem(tab: MainTab, isSelected: Boolean, onClick: () -> Unit) {
    Column(
            modifier = Modifier.width(70.dp).clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                tint = if (isSelected) Color.White else Color(0xFF7A7A7A),
                modifier = Modifier.size(22.dp)
        )

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
