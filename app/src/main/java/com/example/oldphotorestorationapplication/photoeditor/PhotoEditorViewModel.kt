package com.example.oldphotorestorationapplication.photoeditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.oldphotorestorationapplication.data.PhotoDatabase
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.network.NetworkRepository
import com.example.oldphotorestorationapplication.network.RestorationNetwork
import kotlinx.coroutines.*

class PhotoEditorViewModel (application: Application) : AndroidViewModel(application) {
    val allPhotos: LiveData<List<Photo>>
    val allFaces: LiveData<List<Face>>
    private val repositoryPhoto: PhotoRepository

    init {
        val photoDao = PhotoDatabase.getDatabase().photoDao()
        val faceDao = PhotoDatabase.getDatabase().faceDao()
        val photoAndFacesDao = PhotoDatabase.getDatabase().photoAndFacesDao()
        repositoryPhoto = PhotoRepository(photoDao, faceDao, photoAndFacesDao)
        allPhotos = repositoryPhoto.readAllPhoto
        allFaces = repositoryPhoto.readAllFaces
    }

    fun updatePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repositoryPhoto.updatePhoto(photo) }
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repositoryPhoto.deletePhoto(photo) }
    }

    fun findPhotoById(id: Long): LiveData<Photo> {
        return repositoryPhoto.findPhotoById(id)
    }

    fun findFacesByPhotoId(idPhoto: Long): LiveData<List<Face>> {
        return repositoryPhoto.findFacesByPhotoId(idPhoto)
    }

    fun updateFace(face: Face) {
        viewModelScope.launch(Dispatchers.IO) { repositoryPhoto.updateFace(face) }
    }
}