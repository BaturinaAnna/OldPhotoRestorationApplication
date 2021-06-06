package com.example.oldphotorestorationapplication.network

class NetworkRepository(private val restorationNetwork: RestorationNetwork) {

    suspend fun restorePhoto(imagePath: String, removeScratches: String): List<ByteArray>{
        return restorationNetwork.restorePhoto(imagePath, removeScratches)
    }

}