package com.example.oldphotorestorationapplication.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoViewModel(application: Application) : AndroidViewModel(application) {
    val allData: LiveData<List<Photo>>
    private val repository: PhotoRepository

    init {
        val photoDao = PhotoDatabase.getDatabase().photoDao()
        repository = PhotoRepository(photoDao)
        allData = repository.readAllData
    }

    fun addPhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repository.addPhoto(photo) }
    }

    fun updatePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repository.updatePhoto(photo) }
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repository.deletePhoto(photo) }
    }

    fun findPhotoById(id: Int): LiveData<Photo> {
        return repository.findPhotoById(id)
    }
}
