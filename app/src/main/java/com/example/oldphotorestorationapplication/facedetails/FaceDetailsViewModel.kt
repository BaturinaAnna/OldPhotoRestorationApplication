package com.example.oldphotorestorationapplication.facedetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.oldphotorestorationapplication.data.PhotoDatabase
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.data.photowithfaces.PhotoWithFaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FaceDetailsViewModel(application: Application) : AndroidViewModel(application){
//    val allFacesWithNames: LiveData<List<Face>>
    val allPhotoWithFaces: LiveData<List<PhotoWithFaces>>
    private val repositoryPhoto: PhotoRepository

    init {
        val photoDao = PhotoDatabase.getDatabase().photoDao()
        val faceDao = PhotoDatabase.getDatabase().faceDao()
        val photoAndFacesDao = PhotoDatabase.getDatabase().photoAndFacesDao()
        repositoryPhoto = PhotoRepository(photoDao, faceDao, photoAndFacesDao)
        allPhotoWithFaces = repositoryPhoto.readAllPhotoWithFaces
//        allFacesWithNames = repositoryPhoto.readAllFacesWithNames
    }

    fun findFaceById(id: Long): LiveData<Face> {
        return repositoryPhoto.findFaceById(id)
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repositoryPhoto.deletePhoto(photo) }
    }

//    fun findPhotosByName(name: String): LiveData<List<Photo>>{
//        return repositoryPhoto.findPhotosByName(name)
//    }
}
