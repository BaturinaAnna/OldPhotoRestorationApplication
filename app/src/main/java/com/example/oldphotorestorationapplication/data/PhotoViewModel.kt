package com.example.oldphotorestorationapplication.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoViewModel(application: Application): AndroidViewModel(application) {
    private val readAllData: LiveData<List<Photo>>
    private val repository: PhotoRepository

    init{
        val photoDao = PhotoDatabase.getDatabase(application).photoDao()
        repository = PhotoRepository(photoDao)
        readAllData = repository.readAllData
    }

    fun addPhoto(photo: Photo){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPhoto(photo)
        }
    }
}