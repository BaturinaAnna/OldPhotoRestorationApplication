package com.example.oldphotorestorationapplication.photoeditor

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class PhotoEditorViewModel @Inject constructor(
    private val repositoryPhoto: PhotoRepository
) : ViewModel() {
    val allFaces: LiveData<List<Face>> = repositoryPhoto.readAllFaces

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