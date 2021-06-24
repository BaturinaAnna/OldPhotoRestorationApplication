package com.example.oldphotorestorationapplication.photogallery

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.imagepickerlibrary.dateFormatForTakePicture
import com.example.oldphotorestorationapplication.data.PhotoDatabase
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.data.photowithfaces.PhotoWithFaces
import com.example.oldphotorestorationapplication.firebase.firebaseauth.FirebaseAuthRepository
import com.example.oldphotorestorationapplication.firebase.realtimedatabase.FirebaseRealtimeDatabaseRepository
import com.example.oldphotorestorationapplication.getBitmapFromURL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoGalleryViewModel (application: Application) : AndroidViewModel(application) {
    val allPhotos: LiveData<List<Photo>>
    val allPhotoWithFaces: LiveData<List<PhotoWithFaces>>
    private val repository: PhotoRepository
    // FOR FIREBASE
    private val firebaseRealtimeDatabaseRepository: FirebaseRealtimeDatabaseRepository
    private val firebaseAuthRepository: FirebaseAuthRepository

    init {
        val photoDao = PhotoDatabase.getDatabase().photoDao()
        val faceDao = PhotoDatabase.getDatabase().faceDao()
        val photoAndFacesDao = PhotoDatabase.getDatabase().photoAndFacesDao()
        repository = PhotoRepository(photoDao, faceDao, photoAndFacesDao)
        allPhotos = repository.readAllPhoto
        allPhotoWithFaces = repository.readAllPhotoWithFaces

        firebaseRealtimeDatabaseRepository = FirebaseRealtimeDatabaseRepository()
        firebaseAuthRepository = FirebaseAuthRepository()
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) { repository.deletePhoto(photo) }
    }

//    fun getAllPhoto(): LiveData<List<Photo>>{
//        return firebaseRealtimeDatabaseRepository.getAllPhotos(firebaseAuthRepository.getCurrentUserId()!!)
////        irebaseRealtimeDatabaseRepository.getAllPhotos(firebaseAuthRepository.getCurrentUserId()!!)
//    }
}