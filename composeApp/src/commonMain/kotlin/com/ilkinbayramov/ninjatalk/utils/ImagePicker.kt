package com.ilkinbayramov.ninjatalk.utils

expect class ImagePicker() {
    fun pickImage(onImageSelected: (ByteArray) -> Unit)
}
