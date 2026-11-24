package com.ilkinbayramov.psst.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilkinbayramov.psst.ui.theme.NinjaBackground
import com.ilkinbayramov.psst.ui.theme.NinjaPrimary

private data class EmojiCategory(val icon: String, val emojis: List<String>)

private fun String.toEmojiList(): List<String> = split(" ").filter { it.isNotBlank() }

private val emojiCategories =
        listOf(
                EmojiCategory(
                        "😀",
                        ("😀 😃 😄 😁 😆 😅 🤣 😂 🙂 🙃 😉 😊 😇 🥰 😍 🤩 😘 😗 😚 😙 " +
                                        "😋 😛 😜 🤪 😝 🤑 🤗 🤭 🤫 🤔 🤐 🤨 😐 😑 😶 😏 😒 🙄 😬 😌 " +
                                        "😔 😪 🤤 😴 😷 🤒 🤕 🤢 🤮 🥵 🥶 🥴 😵 🤯 🤠 🥳 😎 🤓 🧐 😕 " +
                                        "😟 🙁 😮 😯 😲 😳 🥺 😦 😧 😨 😰 😥 😢 😭 😱 😖 😣 😞 😓 😩 " +
                                        "😫 🥱 😤 😡 😠 🤬 😈 👿 💀 💩 🤡 👻 👽 🤖")
                                .toEmojiList()
                ),
                EmojiCategory(
                        "👍",
                        ("👋 🤚 ✋ 🖖 👌 🤏 ✌️ 🤞 🤟 🤘 🤙 👈 👉 👆 👇 ☝️ 👍 👎 ✊ 👊 " +
                                        "🤛 🤜 👏 🙌 👐 🤲 🤝 🙏 💪 🦾 👀 👁️ 👅 👄 💋 🫶 🙋 🙅 🙆 🤷 " +
                                        "🤦 💁 🙇 🧑 👦 👧 👨 👩 🧔 👴 👵 🥷")
                                .toEmojiList()
                ),
                EmojiCategory(
                        "❤️",
                        ("❤️ 🧡 💛 💚 💙 💜 🖤 🤍 🤎 💔 ❣️ 💕 💞 💓 💗 💖 💘 💝 💟 ♥️ " +
                                        "💯 💢 💥 💫 💦 💨 🔥 ✨ ⭐ 🌟 ⚡ 💤 💬 💭 🎉 🎊 🎁 🎈 🏆 🥇")
                                .toEmojiList()
                ),
                EmojiCategory(
                        "🐶",
                        ("🐶 🐱 🐭 🐹 🐰 🦊 🐻 🐼 🐨 🐯 🦁 🐮 🐷 🐸 🐵 🙈 🙉 🙊 🐔 🐧 " +
                                        "🐦 🦄 🐝 🦋 🐌 🐞 🐢 🐍 🐙 🐬 🐳 🦈 🌸 🌹 🌻 🌷 🌱 🌲 🍀 🍁 " +
                                        "🌙 ☀️ 🌈 ☁️ ❄️ 🌊")
                                .toEmojiList()
                ),
                EmojiCategory(
                        "🍕",
                        ("🍏 🍎 🍐 🍊 🍋 🍌 🍉 🍇 🍓 🍒 🍑 🥭 🍍 🥥 🥝 🍅 🥑 🍆 🌶️ 🌽 " +
                                        "🍞 🧀 🍳 🥞 🥓 🍔 🍟 🍕 🌭 🥪 🌮 🌯 🍝 🍜 🍣 🍩 🍪 🎂 🍰 🍫 " +
                                        "🍬 🍭 🍿 ☕ 🍵 🥤 🍺 🍷 🥂 🍾")
                                .toEmojiList()
                ),
                EmojiCategory(
                        "⚽",
                        ("⚽ 🏀 🏈 ⚾ 🎾 🏐 🏓 🥊 🎮 🎲 🎯 🎳 🎸 🎹 🎤 🎧 🎬 🎨 🚗 ✈️ " +
                                        "🚀 🏠 🏖️ 🗽 📱 💻 📷 💡 📚 ✏️ 📌 🔒 🔑 💰 💎 ⏰ 📍 ✅ ❌ ❓ " +
                                        "❗ 🆗 🆒 🔞")
                                .toEmojiList()
                )
        )

@Composable
fun EmojiPicker(onEmojiSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    var selectedCategory by remember { mutableStateOf(0) }

    Column(modifier = modifier.fillMaxWidth().height(280.dp).background(NinjaBackground)) {
        // Category tabs
        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            emojiCategories.forEachIndexed { index, category ->
                Box(
                        modifier =
                                Modifier.size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                                if (index == selectedCategory)
                                                        NinjaPrimary.copy(alpha = 0.25f)
                                                else Color.Transparent
                                        )
                                        .clickable { selectedCategory = index },
                        contentAlignment = Alignment.Center
                ) { Text(category.icon, fontSize = 20.sp) }
            }
        }

        LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 44.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(emojiCategories[selectedCategory].emojis) { emoji ->
                Box(
                        modifier =
                                Modifier.size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onEmojiSelected(emoji) },
                        contentAlignment = Alignment.Center
                ) { Text(emoji, fontSize = 26.sp) }
            }
        }
    }
}
