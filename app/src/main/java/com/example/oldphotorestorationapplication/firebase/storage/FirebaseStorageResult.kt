package com.example.oldphotorestorationapplication.firebase.storage

sealed class FirebaseStorageResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : FirebaseStorageResult<T>(data)
    class Error<T>(message: String?, data: T? = null) : FirebaseStorageResult<T>(data, message)
}