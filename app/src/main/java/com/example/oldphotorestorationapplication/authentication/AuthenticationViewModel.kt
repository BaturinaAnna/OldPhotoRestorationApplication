package com.example.oldphotorestorationapplication.authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.oldphotorestorationapplication.firebase.AuthResult
import com.example.oldphotorestorationapplication.firebase.FireBaseRepository
import com.google.firebase.auth.FirebaseUser

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {
    private val fireBaseRepository: FireBaseRepository = FireBaseRepository()

    fun checkIfCurrentUser(): Boolean{
        return fireBaseRepository.checkIfCurrentUser()
    }

    fun signUpUser(email: String, password: String): LiveData<AuthResult<FirebaseUser?>> {
        return fireBaseRepository.signUpUser(email, password)
    }

    fun signInUser(email: String, password: String): LiveData<AuthResult<FirebaseUser?>> {
        return fireBaseRepository.signInUser(email, password)
    }

}