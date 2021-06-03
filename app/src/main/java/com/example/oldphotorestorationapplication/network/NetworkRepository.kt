package com.example.oldphotorestorationapplication.network

class NetworkRepository(private val restorationNetwork: RestorationNetwork) {

    suspend fun restorePhoto(imagePath: String,
                             removeScratches: String,
                             url: String): List<ByteArray>{
        return restorationNetwork.uploadPhoto(imagePath, removeScratches, url)
    }

}