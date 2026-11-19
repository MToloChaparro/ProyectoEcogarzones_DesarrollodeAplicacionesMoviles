package com.example.gestionecogarzones.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
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

    fun copiarDocumentoAArchivosInternos(context: Context, uri: Uri): Pair<Uri, String>? {
        return try {
            val contentResolver = context.contentResolver
            var fileName = "cronograma_${System.currentTimeMillis()}"
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex)
                }
            }
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File(context.filesDir, "doc_${System.currentTimeMillis()}_$fileName")
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            Pair(Uri.fromFile(file), fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun abrirDocumento(context: Context, uriString: String) {
        try {
            val uri = Uri.parse(uriString)
            val file = if (uri.scheme == "file") File(uri.path ?: "") else File(uri.toString())
            if (!file.exists()) {
                Toast.makeText(context, "El archivo ya no existe en el almacenamiento", Toast.LENGTH_SHORT).show()
                return
            }
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val extension = file.extension.lowercase()
            val mimeType = when (extension) {
                "pdf" -> "application/pdf"
                "doc", "docx" -> "application/msword"
                else -> context.contentResolver.getType(contentUri) ?: "*/*"
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se encontró una aplicación para abrir el documento: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
