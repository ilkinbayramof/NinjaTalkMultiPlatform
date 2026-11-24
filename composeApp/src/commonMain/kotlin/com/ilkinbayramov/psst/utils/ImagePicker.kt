package com.ilkinbayramov.psst.utils

expect class ImagePicker() {
    fun pickImage(onImageSelected: (ByteArray) -> Unit)
}
