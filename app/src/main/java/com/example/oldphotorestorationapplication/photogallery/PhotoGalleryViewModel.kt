package com.example.oldphotorestorationapplication.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.data.photowithfaces.PhotoWithFaces
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(
    private val repository: PhotoRepository
) : ViewModel() {
    val allPhotos: LiveData<List<Photo>> = repository.readAllPhoto
    val allPhotoWithFaces: LiveData<List<PhotoWithFaces>> = repository.readAllPhotoWithFaces

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repository.deletePhoto(photo) }
    }
}