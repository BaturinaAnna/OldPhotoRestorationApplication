package com.example.oldphotorestorationapplication.galleries

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.oldphotorestorationapplication.firebase.firebaseauth.FirebaseAuthRepository

class GalleriesViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseAuthRepository: FirebaseAuthRepository = FirebaseAuthRepository()

    fun signOut(){
        firebaseAuthRepository.signOut()
    }

}