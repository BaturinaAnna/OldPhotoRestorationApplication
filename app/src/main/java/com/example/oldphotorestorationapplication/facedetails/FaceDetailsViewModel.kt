package com.example.oldphotorestorationapplication.facedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.data.photowithfaces.PhotoWithFaces
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceDetailsViewModel @Inject constructor(
    private val repositoryPhoto: PhotoRepository
) : ViewModel(){

    val allPhotoWithFaces: LiveData<List<PhotoWithFaces>> = repositoryPhoto.readAllPhotoWithFaces

    fun findFaceById(id: Long): LiveData<Face> {
        return repositoryPhoto.findFaceById(id)
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repositoryPhoto.deletePhoto(photo) }
    }
}
