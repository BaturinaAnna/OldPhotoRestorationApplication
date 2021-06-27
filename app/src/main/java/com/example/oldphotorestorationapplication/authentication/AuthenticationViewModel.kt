package com.example.oldphotorestorationapplication.authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.oldphotorestorationapplication.firebaseAuth.AuthResult
import com.example.oldphotorestorationapplication.firebaseAuth.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseUser

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseAuthRepository: FirebaseAuthRepository = FirebaseAuthRepository()

    fun checkIfCurrentUser(): Boolean{
        return firebaseAuthRepository.checkIfCurrentUser()
    }

    fun signUpUser(email: String, password: String): LiveData<AuthResult<FirebaseUser?>> {
        return firebaseAuthRepository.signUpUser(email, password)
    }

    fun signInUser(email: String, password: String): LiveData<AuthResult<FirebaseUser?>> {
        return firebaseAuthRepository.signInUser(email, password)
    }

}