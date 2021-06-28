package com.example.oldphotorestorationapplication.network

import javax.inject.Inject

class NetworkRepository
    @Inject constructor(private val restorationNetwork: RestorationNetwork) {

    suspend fun restorePhoto(imagePath: String, removeScratches: String): List<ByteArray>{
        return restorationNetwork.restorePhoto(imagePath, removeScratches)
    }

}