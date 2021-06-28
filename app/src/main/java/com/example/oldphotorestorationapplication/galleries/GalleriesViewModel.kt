package com.example.oldphotorestorationapplication.galleries

import androidx.lifecycle.ViewModel
import com.example.oldphotorestorationapplication.firebaseAuth.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleriesViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
): ViewModel() {

    fun signOut(){
        firebaseAuthRepository.signOut()
    }

}