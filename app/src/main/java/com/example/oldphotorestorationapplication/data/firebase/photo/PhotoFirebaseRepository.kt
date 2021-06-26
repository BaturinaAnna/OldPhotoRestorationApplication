package com.example.oldphotorestorationapplication.data.firebase.photo

class PhotoFirebaseRepository(
    private val photoFirebaseDao: PhotoFirebaseDao,
    private val photoItemRemoteKeysDao: PhotoItemRemoteKeysDao
) {

    suspend fun updatePhoto(photo: PhotoFirebase) {
        return photoFirebaseDao.updatePhoto(photo)
    }

}