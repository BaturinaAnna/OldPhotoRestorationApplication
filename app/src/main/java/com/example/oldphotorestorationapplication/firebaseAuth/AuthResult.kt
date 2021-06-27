package com.example.oldphotorestorationapplication.firebaseAuth

sealed class AuthResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : AuthResult<T>(data)
    class Error<T>(message: String?, data: T? = null) : AuthResult<T>(data, message)
}