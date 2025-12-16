package com.ilkinbayramov.ninjatalk.ui.settings.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ilkinbayramov.ninjatalk.data.ApiClient
import com.ilkinbayramov.ninjatalk.data.TokenManager
import com.ilkinbayramov.ninjatalk.data.repository.UserRepository
import com.ilkinbayramov.ninjatalk.ui.theme.*
import com.ilkinbayramov.ninjatalk.utils.ImagePicker
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable data class UpdateBioRequest(val bio: String)

expect @Composable fun rememberImagePicker(): ImagePicker

@Composable
fun ProfileEditScreen(onBackClick: () -> Unit) {
    val imagePicker = rememberImagePicker()

    var bio by remember {
        mutableStateOf("Yeni insanlarla tanışmayı seviyorum. Bana bir mesaj gönder!")
    }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isUploadingImage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val tokenManager = remember { TokenManager() }
    val userRepository = remember { UserRepository() }

    LaunchedEffect(Unit) {
        val token = tokenManager.getToken()
        if (token != null) {
            isLoading = true
            userRepository
                    .getMe(token)
                    .onSuccess { user ->
                        user.bio?.let { bio = it }
                        user.profileImageUrl?.let { profileImageUrl = it }
                        isLoading = false
                    }
                    .onFailure {
                        snackbarHostState.showSnackbar("Profil yüklenemedi: ${it.message}")
                        isLoading = false
                    }
        }
    }

    // Handle image upload when selected
    LaunchedEffect(selectedImageBytes) {
        selectedImageBytes?.let { bytes ->
            val token = tokenManager.getToken()
            if (token != null) {
                isUploadingImage = true
                userRepository
                        .uploadProfileImage(token, bytes)
                        .onSuccess { imageUrl ->
                            profileImageUrl = imageUrl
                            selectedImageBytes = null
                            isUploadingImage = false
                            snackbarHostState.showSnackbar("Profil fotoğrafı güncellendi!")
                        }
                        .onFailure {
                            snackbarHostState.showSnackbar("Fotoğraf yüklenemedi: ${it.message}")
                            selectedImageBytes = null
                            isUploadingImage = false
                        }
            }
        }
    }

    Scaffold(
            containerColor = NinjaBackground,
            snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Top Bar
            Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                    )
                }

                Text(
                        text = "Profil Düzenle",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                )
            }

            Column(
                    modifier =
                            Modifier.fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Photo
                Box(modifier = Modifier.size(120.dp)) {
                    // Avatar Circle with image or placeholder
                    Box(
                            modifier =
                                    Modifier.size(120.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE8C4A0)),
                            contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUrl != null) {
                            AsyncImage(
                                    model = "${ApiClient.getBaseUrl()}$profileImageUrl",
                                    contentDescription = "Profile Image",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(text = "👤", fontSize = 60.sp)
                        }

                        // Upload indicator
                        if (isUploadingImage) {
                            Box(
                                    modifier =
                                            Modifier.fillMaxSize()
                                                    .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    // Edit Button
                    Box(
                            modifier =
                                    Modifier.size(36.dp)
                                            .clip(CircleShape)
                                            .background(NinjaPrimary)
                                            .align(Alignment.BottomEnd)
                                            .clickable {
                                                imagePicker.pickImage { imageBytes ->
                                                    selectedImageBytes = imageBytes
                                                }
                                            },
                            contentAlignment = Alignment.Center
                    ) {
                        Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Düzenle",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Biyografi Section
                Text(
                        text = "Biyografi",
                        color = NinjaTextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = NinjaSurface,
                                        unfocusedContainerColor = NinjaSurface,
                                        cursorColor = NinjaPrimary,
                                        focusedBorderColor = NinjaPrimary,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                ),
                        maxLines = 4
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Kaydet Button
                Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                try {
                                    val token = tokenManager.getToken()
                                    if (token == null) {
                                        snackbarHostState.showSnackbar(
                                                "Oturum süresi dolmuş, lütfen tekrar giriş yapın"
                                        )
                                        return@launch
                                    }

                                    val response =
                                            ApiClient.httpClient.put(
                                                    "${ApiClient.getBaseUrl()}/api/users/bio"
                                            ) {
                                                contentType(ContentType.Application.Json)
                                                header("Authorization", "Bearer $token")
                                                setBody(UpdateBioRequest(bio))
                                            }

                                    if (response.status == HttpStatusCode.OK) {
                                        snackbarHostState.showSnackbar("Profil güncellendi!")
                                        onBackClick()
                                    } else {
                                        snackbarHostState.showSnackbar(
                                                "Hata: ${response.bodyAsText()}"
                                        )
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Hata: ${e.message}")
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NinjaPrimary),
                        enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Kaydet", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
