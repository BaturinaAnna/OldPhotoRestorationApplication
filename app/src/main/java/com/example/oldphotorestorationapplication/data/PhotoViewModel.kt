package com.example.oldphotorestorationapplication.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class PhotoViewModel(application: Application) : AndroidViewModel(application) {
    val allData: LiveData<List<Photo>>
    private val repository: PhotoRepository

    init {
        val photoDao = PhotoDatabase.getDatabase().photoDao()
        val faceDao = PhotoDatabase.getDatabase().faceDao()
        repository = PhotoRepository(photoDao, faceDao)
        allData = repository.readAllData
    }

    fun addPhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repository.addPhoto(photo) }
    }

    fun addPhotoWithFaces(photo: Photo, faces: List<Face>){
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch{
                val idInsertedPhoto = repository.addPhoto(photo)
                for(face in faces){
                    face.idPhoto = idInsertedPhoto
                    repository.addFace(face)
                }
            }
    }

    fun updatePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repository.updatePhoto(photo) }
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repository.deletePhoto(photo) }
    }

    fun findPhotoById(id: Long): LiveData<Photo> {
        return repository.findPhotoById(id)
    }

    fun  findFacesByPhotoId(idPhoto: Long): LiveData<List<Face>> {
        return repository.findFacesByPhotoId(idPhoto)
    }
}
