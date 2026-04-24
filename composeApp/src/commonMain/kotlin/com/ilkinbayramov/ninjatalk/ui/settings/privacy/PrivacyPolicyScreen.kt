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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBackClick: () -> Unit) {
    Scaffold(
        containerColor = NinjaBackground,
        topBar = {
            TopAppBar(
                title = { Text("Gizlilik Politikası", color = Color.White) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Son Güncelleme: 24 Nisan 2026",
                color = NinjaTextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            PolicySection(
                title = "1. Toplanan Veriler",
                content = "NinjaTalk uygulamasını kullanırken e-posta adresiniz, cinsiyetiniz, doğum tarihiniz, biyografiniz ve profil fotoğrafınız uygulamanın temel işlevlerini sağlamak amacıyla toplanmaktadır. Ayrıca, mesajlaşma geçmişiniz ve engellediğiniz kullanıcı bilgileri güvenli bir şekilde sunucularımızda saklanmaktadır."
            )

            PolicySection(
                title = "2. Veri Kullanımı",
                content = "Toplanan veriler yalnızca hesabınızı yönetmek, diğer kullanıcılarla iletişim kurmanızı sağlamak (mesajlaşma) ve kişiselleştirilmiş bir deneyim sunmak için kullanılır. Verileriniz hiçbir şekilde izniniz olmadan 3. şahıslarla paylaşılmaz."
            )

            PolicySection(
                title = "3. Veri Güvenliği",
                content = "Kullanıcı bilgileri, mesajlaşmalar ve oturum açma tokenları, yetkisiz erişime karşı korunmak için güvenli sunucularda barındırılmakta ve şifreli bir şekilde iletilmektedir."
            )

            PolicySection(
                title = "4. Hesap Silme ve Veri İptali",
                content = "İstediğiniz zaman Ayarlar bölümünden 'Hesabı Sil' seçeneğini kullanarak hesabınızı ve ilişkili tüm verilerinizi (mesajlar, profil bilgileri, ayarlar) kalıcı olarak silebilirsiniz. Hesap silindikten sonra verilerin geri getirilmesi mümkün değildir."
            )

            PolicySection(
                title = "5. Uygulama İzinleri",
                content = "Uygulama, size yeni mesajları anında bildirebilmek için bildirim izni isteyebilir. Bu izinleri cihaz ayarlarınızdan veya uygulama içi Ayarlar bölümünden dilediğiniz zaman yönetebilirsiniz."
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
