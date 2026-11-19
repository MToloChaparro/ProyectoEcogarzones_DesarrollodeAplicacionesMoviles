package com.example.gestionecogarzones.utils

import android.content.Context
import android.net.Uri
import java.io.File

object FileUtils {
    fun copiarUriAArchivosInternos(context: Context, uri: Uri, prefix: String): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.filesDir, "${prefix}_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
