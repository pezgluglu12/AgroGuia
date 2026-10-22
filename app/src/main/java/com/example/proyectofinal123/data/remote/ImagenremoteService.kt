package com.example.proyectofinal123.data.remote

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ImagenremoteService {

    companion object {
        // Le quité el espacio en blanco que tenías antes: " yh12mfc5" -> "yh12mfc5"
        private const val CLOUD_NAME = "yh12mfc5" 
        private const val UPLOAD_PRESET = "persona_android"
    }

    /**
     * Recibe un Bitmap (la foto que toma la cámara), la convierte a bytes 
     * y la sube directamente a Cloudinary. 
     * Retorna un Result con la URL (String) de la imagen subida.
     */
    suspend fun subirImagenCloudinary(bitmap: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 1. Convertimos la imagen Bitmap a ByteArray para poder subirla
            val outputStream = ByteArrayOutputStream()
            // Comprimimos al 80% para que no sea tan pesada (Cloudinary tiene límites gratuitos)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val bytes = outputStream.toByteArray()

            require(bytes.isNotEmpty()) { "La imagen generada está vacía" }
            require(bytes.size <= 5 * 1024 * 1024) { "La imagen supera el límite de 5 MB" }

            // 2. Preparamos la conexión HTTP (Sin dependencias externas, nativo de Android/Java)
            val boundary = "----AndroidCloudinary${System.currentTimeMillis()}"
            val endpoint = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
            
            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 15000
                readTimeout = 30000
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            }

            // 3. Escribimos la información y el archivo en la petición
            try {
                DataOutputStream(connection.outputStream).use { output ->
                    fun campo(nombre: String, valor: String) {
                        output.writeBytes("--$boundary\r\n")
                        output.writeBytes("Content-Disposition: form-data; name=\"$nombre\"\r\n\r\n")
                        output.write(valor.toByteArray(Charsets.UTF_8))
                        output.writeBytes("\r\n")
                    }

                    campo("upload_preset", UPLOAD_PRESET)
                    campo("folder", "cultivos_agroguia") // Guardar en una carpeta específica

                    output.writeBytes("--$boundary\r\n")
                    output.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"enfermedad_${System.currentTimeMillis()}.jpg\"\r\n")
                    output.writeBytes("Content-Type: image/jpeg\r\n\r\n")
                    output.write(bytes)
                    output.writeBytes("\r\n--$boundary--\r\n")
                    output.flush()
                }

                // 4. Verificamos la respuesta de Cloudinary
                val status = connection.responseCode
                val responseStream = if (status in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val response = responseStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                
                if (status !in 200..299) {
                    Log.e("CLOUDINARY", "Cloudinary HTTP $status: $response")
                    throw Exception("Cloudinary rechazó la imagen (HTTP $status)")
                }

                // 5. Parseamos el JSON para obtener la URL
                val json = JSONObject(response)
                val secureUrl = json.optString("secure_url")
                require(secureUrl.startsWith("https://")) { "Cloudinary no devolvió una URL HTTPS válida" }

                Log.d("CLOUDINARY", "Imagen subida exitosamente: $secureUrl")
                Result.success(secureUrl)

            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            Log.e("CLOUDINARY", "Error subiendo la imagen", e)
            Result.failure(e)
        }
    }
}