package com.example.oldphotorestorationapplication.network

import android.util.Log
import java.io.File
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.lang.NullPointerException
import java.util.zip.ZipInputStream

class RestorationNetwork {

    suspend fun uploadPhoto(
        imagePath: String,
        removeScratches: String,
        url: String
    ): List<ByteArray> =
        withContext(Dispatchers.IO) {
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

            val response: Response = call.execute()
            if (response.isSuccessful) {
                return@withContext ZipInputStream(
                    response.body()?.byteStream()
                ).use { zipInputStream ->
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
