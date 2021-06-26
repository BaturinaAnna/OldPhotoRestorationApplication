package com.example.oldphotorestorationapplication.photoeditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.oldphotorestorationapplication.data.PhotoDatabase
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.firebase.photo.PhotoFirebase
import com.example.oldphotorestorationapplication.data.firebase.photo.PhotoFirebaseDatabase
import com.example.oldphotorestorationapplication.data.firebase.photo.PhotoFirebaseRepository
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.firebase.firebaseauth.FirebaseAuthRepository
import com.example.oldphotorestorationapplication.firebase.realtimedatabase.FirebaseRealtimeDatabaseRepository
import com.example.oldphotorestorationapplication.mediator.RemoteRepository
import com.example.oldphotorestorationapplication.network.NetworkRepository
import com.example.oldphotorestorationapplication.network.RestorationNetwork
import kotlinx.coroutines.*

class PhotoEditorViewModel (application: Application) : AndroidViewModel(application) {

    private val firebaseRealtimeDatabaseRepository = FirebaseRealtimeDatabaseRepository()
    private val firebaseAuthRepository = FirebaseAuthRepository()
    private val photoFirebaseRepository = PhotoFirebaseRepository(
        PhotoFirebaseDatabase.getDatabase().photoFirebaseDao(),
        PhotoFirebaseDatabase.getDatabase().photoItemRemoteKeysDao()
    )

    fun getPhotoById(idPhoto: String): LiveData<PhotoFirebase> {
        return firebaseRealtimeDatabaseRepository.getPhotoById(
            idUser = firebaseAuthRepository.getCurrentUserId()!!,
            idPhoto = idPhoto
        )
    }

    fun updatePhotoInfo(photo: PhotoFirebase){
        val idCurrentUser = firebaseAuthRepository.getCurrentUserId()!!

        viewModelScope.launch {
            photoFirebaseRepository.updatePhoto(photo)
        }

        firebaseRealtimeDatabaseRepository.setPhotoInfo(
            idUser = idCurrentUser,
            idPhoto = photo.idPhoto,
            infoType = "title",
            info = photo.title ?: ""
        )

        firebaseRealtimeDatabaseRepository.setPhotoInfo(
            idUser = idCurrentUser,
            idPhoto = photo.idPhoto,
            infoType = "description",
            info = photo.description ?: ""
        )

        firebaseRealtimeDatabaseRepository.setPhotoInfo(
            idUser = idCurrentUser,
            idPhoto = photo.idPhoto,
            infoType = "date",
            info = photo.date ?: ""
        )

        firebaseRealtimeDatabaseRepository.setPhotoInfo(
            idUser = idCurrentUser,
            idPhoto = photo.idPhoto,
            infoType = "location",
            info = photo.location ?: ""
        )
    }

//    fun updatePhoto(photo: PhotoFirebase){
//        val idUser = firebaseAuthRepository.getCurrentUserId()!!
//        val idPhoto = photo.idPhoto
//        firebaseRealtimeDatabaseRepository.setPhotoInfo(
//            idUser = idUser,
//            idPhoto = idPhoto,
//            infoType = "title",
//            info = photo.title.toString()
//        )
//        firebaseRealtimeDatabaseRepository.setPhotoInfo(
//            idUser = idUser,
//            idPhoto = idPhoto,
//            infoType = "",
//            info =
//        )
//    }


////    val allPhotos: LiveData<List<Photo>>
//    val allFaces: LiveData<List<Face>>
//    private val repositoryPhoto: PhotoRepository

//    init {
//        val photoDao = PhotoDatabase.getDatabase().photoDao()
//        val faceDao = PhotoDatabase.getDatabase().faceDao()
//        val photoAndFacesDao = PhotoDatabase.getDatabase().photoAndFacesDao()
//        repositoryPhoto = PhotoRepository(photoDao, faceDao, photoAndFacesDao)
////        allPhotos = repositoryPhoto.readAllPhoto
//        allFaces = repositoryPhoto.readAllFaces
//    }
//
//    fun updatePhoto(photo: Photo) {
//        viewModelScope.launch(Dispatchers.IO) { repositoryPhoto.updatePhoto(photo) }
//    }
//
//    fun deletePhoto(photo: Photo) {
//        viewModelScope.launch(Dispatchers.IO) { repositoryPhoto.deletePhoto(photo) }
//    }
//
//    fun findPhotoById(id: Long): LiveData<Photo> {
//        return repositoryPhoto.findPhotoById(id)
//    }
//
//    fun findFacesByPhotoId(idPhoto: Long): LiveData<List<Face>> {
//        return repositoryPhoto.findFacesByPhotoId(idPhoto)
//    }
//
//    fun updateFace(face: Face) {
//        viewModelScope.launch(Dispatchers.IO) { repositoryPhoto.updateFace(face) }
//    }
}