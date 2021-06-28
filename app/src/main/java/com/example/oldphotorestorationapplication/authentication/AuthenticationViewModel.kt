package com.example.oldphotorestorationapplication.authentication

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.oldphotorestorationapplication.firebaseAuth.AuthResult
import com.example.oldphotorestorationapplication.firebaseAuth.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    fun checkIfCurrentUser(): Boolean {
        return firebaseAuthRepository.checkIfCurrentUser()
    }

    fun signUpUser(email: String, password: String): LiveData<AuthResult<FirebaseUser?>> {
        return firebaseAuthRepository.signUpUser(email, password)
    }

    fun signInUser(email: String, password: String): LiveData<AuthResult<FirebaseUser?>> {
        return firebaseAuthRepository.signInUser(email, password)
    }
}