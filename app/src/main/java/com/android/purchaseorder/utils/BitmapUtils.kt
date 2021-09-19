package com.android.purchaseorder.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class BitmapUtils {
    companion object {
        @Suppress("DEPRECATION")
        fun saveImageToStorage(
            context: Context,
            bitmap: Bitmap,
            filename: String,
            mimeType: String = "image/jpeg",
        ): Uri? {
            val imageOutStream: OutputStream
            val uri: Uri?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                context.contentResolver.run {
                    uri =
                        insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return null
                    imageOutStream = openOutputStream(uri) ?: return null
                }
            } else {
                val imagePath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
                val image = File(imagePath, filename)
                // From API 24+
                uri = FileProvider.getUriForFile(context,
                    """${context.applicationContext.packageName}.fileprovider""", image)
                imageOutStream = FileOutputStream(image)
            }
            imageOutStream.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
            return uri
        }
    }
}