package com.example.oldphotorestorationapplication.network

import android.util.Log
import java.io.File
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*

class RestorationNetwork {
    private val url: String =
        "http://192.168.135.55:8080/OldPhotoRestoration_war_exploded/restoration-servlet"

    suspend fun restorePhoto(imagePath: String, removeScratches: String): List<ByteArray> {
        val file = File(imagePath)
        val image: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val requestBody: RequestBody =
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photoToRestore", imagePath, image)
                .addFormDataPart("removeScratches", removeScratches)
                .build()
        val request: Request? = Request.Builder().url(url).post(requestBody).build()
        val okHttpClient: OkHttpClient =
            OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.HOURS)
                .writeTimeout(1, TimeUnit.HOURS)
                .connectTimeout(1, TimeUnit.HOURS)
                .build()
        val call: Call = okHttpClient.newCall(request)
        return withContext(Dispatchers.IO) {
            val response: Response = call.execute()
            if (response.isSuccessful) {
                return@withContext ZipInputStream(response.body()?.byteStream()).use { zipInputStream ->
                    generateSequence { zipInputStream.nextEntry }
                        .filterNot { it.isDirectory }
                        .map { zipInputStream.readBytes() }
                        .toList()
                }
            } else {
                Log.d("ANNA", "Response is not successful")
                throw NullPointerException()
            }
        }
    }
}
