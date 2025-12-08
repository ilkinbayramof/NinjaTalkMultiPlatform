package com.ilkinbayramov.ninjatalk.ui.settings.profile

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.ilkinbayramov.ninjatalk.utils.ImagePicker

@Composable
actual fun rememberImagePicker(): ImagePicker {
    val context = LocalContext.current
    val imagePicker = remember { ImagePicker() }

    val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
                    uri: Uri? ->
                uri?.let { selectedUri ->
                    try {
                        // Take persistable URI permission for reading
                        context.contentResolver.takePersistableUriPermission(
                                selectedUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: SecurityException) {
                        // Permission already granted or not needed
                    }

                    try {
                        val inputStream = context.contentResolver.openInputStream(selectedUri)
                        val bytes = inputStream?.readBytes()
                        inputStream?.close()
                        bytes?.let {
                            // Store callback temporarily
                            imagePicker.onImageSelectedCallback?.invoke(it)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

    imagePicker.setLauncher(launcher)
    return imagePicker
}
