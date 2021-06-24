package com.example.oldphotorestorationapplication.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.oldphotorestorationapplication.data.firebase.photo.PhotoFirebase
import com.example.oldphotorestorationapplication.data.firebase.photo.PhotoFirebaseDatabase
import com.example.oldphotorestorationapplication.firebase.firebaseauth.FirebaseAuthRepository
import com.example.oldphotorestorationapplication.firebase.realtimedatabase.FirebaseRealtimeDatabaseRepository

class RemoteRepository {

    private val db = PhotoFirebaseDatabase.getDatabase()

    @ExperimentalPagingApi
    fun observePhotoListPaginated(): Pager<Int, PhotoFirebase> {
        return Pager(
            config = PagingConfig(10, enablePlaceholders = true),
            remoteMediator = PhotoRemoteMediator(
                db,
                FirebaseRealtimeDatabaseRepository(),
                FirebaseAuthRepository())
        ) {
            db.photoFirebaseDao().observePhotoPaginated()
        }
    }
}