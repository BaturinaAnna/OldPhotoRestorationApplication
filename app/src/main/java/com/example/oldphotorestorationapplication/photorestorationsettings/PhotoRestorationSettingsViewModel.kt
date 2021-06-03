package com.example.oldphotorestorationapplication.photorestorationsettings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.oldphotorestorationapplication.data.PhotoDatabase
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PhotoRestorationSettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PhotoRepository

    init {
        val photoDao = PhotoDatabase.getDatabase().photoDao()
        val faceDao = PhotoDatabase.getDatabase().faceDao()
        repository = PhotoRepository(photoDao, faceDao)
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

}