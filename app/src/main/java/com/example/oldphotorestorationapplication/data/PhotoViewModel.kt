package com.example.oldphotorestorationapplication.data

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PhotoViewModel(application: Application): AndroidViewModel(application) {
    val readAllData: LiveData<List<Photo>>
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

    fun updatePhoto(photo: Photo){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePhoto(photo)
        }
    }

    fun deletePhoto(photo: Photo){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePhoto(photo)
        }
    }

    fun findPhotoById(id: Int): LiveData<Photo>{
         return repository.findPhotoById(id = id)
    }
}