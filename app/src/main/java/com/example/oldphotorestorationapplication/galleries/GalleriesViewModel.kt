package com.example.oldphotorestorationapplication.galleries

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.oldphotorestorationapplication.firebase.FireBaseRepository

class GalleriesViewModel(application: Application) : AndroidViewModel(application) {
    private val fireBaseRepository: FireBaseRepository = FireBaseRepository()

    fun signOut(){
        fireBaseRepository.signOut()
    }

}