package com.ilkinbayramov.ninjatalk.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.UIKit.*
import platform.darwin.NSObject
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual class ImagePicker {
    private var onImageSelected: ((ByteArray) -> Unit)? = null

    actual fun pickImage(onImageSelected: (ByteArray) -> Unit) {
        this.onImageSelected = onImageSelected

        val pickerController = UIImagePickerController()
        pickerController.sourceType =
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        pickerController.delegate =
                object :
                        NSObject(),
                        UIImagePickerControllerDelegateProtocol,
                        UINavigationControllerDelegateProtocol {
                    override fun imagePickerController(
                            picker: UIImagePickerController,
                            didFinishPickingMediaWithInfo: Map<Any?, *>
                    ) {
                        val image =
                                didFinishPickingMediaWithInfo[
                                        UIImagePickerControllerOriginalImage] as?
                                        UIImage
                        image?.let {
                            // Convert UIImage to JPEG data
                            val imageData = UIImageJPEGRepresentation(it, 0.8) // 80% quality
                            imageData?.let { data ->
                                // Convert NSData to ByteArray
                                val byteArray = ByteArray(data.length.toInt())
                                memcpy(byteArray.refTo(0), data.bytes, data.length)
                                this@ImagePicker.onImageSelected?.invoke(byteArray)
                            }
                        }
                        picker.dismissViewControllerAnimated(true, null)
                    }

                    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                        picker.dismissViewControllerAnimated(true, null)
                    }
                }

        // Present picker from root view controller
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(pickerController, true, null)
    }
}
