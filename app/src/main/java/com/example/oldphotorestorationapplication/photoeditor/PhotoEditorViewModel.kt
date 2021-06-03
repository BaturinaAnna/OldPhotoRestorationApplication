package com.example.oldphotorestorationapplication.photoeditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.oldphotorestorationapplication.data.PhotoDatabase
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoEditorViewModel (application: Application) : AndroidViewModel(application) {
    val allPhotos: LiveData<List<Photo>>
    val allFaces: LiveData<List<Face>>
    private val repository: PhotoRepository

    init {
        val photoDao = PhotoDatabase.getDatabase().photoDao()
        val faceDao = PhotoDatabase.getDatabase().faceDao()
        repository = PhotoRepository(photoDao, faceDao)
        allPhotos = repository.readAllPhoto
        allFaces = repository.readAllFaces
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

    fun updateFace(face: Face) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateFace(face) }
    }
}