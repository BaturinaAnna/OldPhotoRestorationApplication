package com.example.oldphotorestorationapplication.gallery

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.oldphotorestorationapplication.App
import com.example.oldphotorestorationapplication.data.PhotoDatabase
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.data.photowithfaces.PhotoWithFaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoGalleryViewModel (application: Application) : AndroidViewModel(application) {
    val allPhotos: LiveData<List<Photo>>
    val allPhotoWithFaces: LiveData<List<PhotoWithFaces>>
    private val repository: PhotoRepository

    init {
        val photoDao = PhotoDatabase.getDatabase().photoDao()
        val faceDao = PhotoDatabase.getDatabase().faceDao()
        val photoAndFacesDao = PhotoDatabase.getDatabase().photoAndFacesDao()
        repository = PhotoRepository(photoDao, faceDao, photoAndFacesDao)
        allPhotos = repository.readAllPhoto
        allPhotoWithFaces = repository.readAllPhotoWithFaces
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repository.deletePhoto(photo) }
    }
}