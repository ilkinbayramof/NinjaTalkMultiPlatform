package com.ilkinbayramov.ninjatalk.utils

import androidx.activity.result.ActivityResultLauncher

actual class ImagePicker {
    private var launcher: ActivityResultLauncher<String>? = null
    var onImageSelectedCallback: ((ByteArray) -> Unit)? = null

    fun setLauncher(launcher: ActivityResultLauncher<String>) {
        this.launcher = launcher
    }

    actual fun pickImage(onImageSelected: (ByteArray) -> Unit) {
        this.onImageSelectedCallback = onImageSelected
        launcher?.launch("image/*")
    }
}
