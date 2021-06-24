package com.example.oldphotorestorationapplication.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.oldphotorestorationapplication.data.firebase.photo.PhotoFirebase
import com.example.oldphotorestorationapplication.data.firebase.photo.PhotoFirebaseDatabase
import com.example.oldphotorestorationapplication.data.firebase.photo.PhotoItemRemoteKeys
import com.example.oldphotorestorationapplication.firebase.firebaseauth.FirebaseAuthRepository
import com.example.oldphotorestorationapplication.firebase.realtimedatabase.FirebaseRealtimeDatabaseRepository
import java.io.InvalidObjectException
import java.lang.Exception

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val db: PhotoFirebaseDatabase,
    private val firebaseRealtimeDatabaseRepository: FirebaseRealtimeDatabaseRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : RemoteMediator<Int, PhotoFirebase>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoFirebase>
    ): MediatorResult {

        return try {

            val initialKey: String? =
                firebaseRealtimeDatabaseRepository.getFirstPhotoId(
                    firebaseAuthRepository.getCurrentUserId()!!
                )

            val id =
                when (loadType) {
                    LoadType.REFRESH -> {
                        val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                        remoteKeys?.photoId ?: initialKey
                    }
                    LoadType.PREPEND -> {
                        return MediatorResult.Success(true)
                    }
                    LoadType.APPEND -> {
                        val remoteKeys =
                            getRemoteKeyForLastItem(state)
                                ?: throw InvalidObjectException("Result is empty")
                        remoteKeys.nextKey ?: return MediatorResult.Success(true)
                    }
                }

            var endOfPaginationReached = true
            id?.let {
                val response =
                    firebaseRealtimeDatabaseRepository.getPhotos(
                        idUser = firebaseAuthRepository.getCurrentUserId()!!,
                        limit = state.config.pageSize,
                        startPhotoId = id
                    )

                endOfPaginationReached = response.size < state.config.pageSize

                db.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        db.photoItemRemoteKeysDao().clearRemoteKeys()
                        db.photoFirebaseDao().deletePhotoItems()
                    }
                    val prevKey =
                        if (id == initialKey) null
                        else
                            firebaseRealtimeDatabaseRepository.getPrevPhotoId(
                                firebaseAuthRepository.getCurrentUserId()!!,
                                id
                            )
                    val nextKey =
                        if (endOfPaginationReached) null
                        else
                            firebaseRealtimeDatabaseRepository.getNextPhotoId(
                                firebaseAuthRepository.getCurrentUserId()!!,
                                id
                            )
                    val keys =
                        response.map {
                            PhotoItemRemoteKeys(
                                photoId = it.idPhoto,
                                prevKey = prevKey,
                                nextKey = nextKey
                            )
                        }
                    db.photoItemRemoteKeysDao().insertAll(keys)
                    db.photoFirebaseDao().insertPhotoList(response)
                }
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            Log.d("ANNA", e.message.toString())
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, PhotoFirebase>
    ): PhotoItemRemoteKeys? {
        return state.lastItemOrNull()?.let { photo ->
            db.withTransaction { db.photoItemRemoteKeysDao().remoteKeysByPhotoId(photo.idPhoto) }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, PhotoFirebase>
    ): PhotoItemRemoteKeys? {
        return state.firstItemOrNull()?.let { photo ->
            db.withTransaction { db.photoItemRemoteKeysDao().remoteKeysByPhotoId(photo.idPhoto) }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, PhotoFirebase>
    ): PhotoItemRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.idPhoto?.let { id ->
                db.withTransaction { db.photoItemRemoteKeysDao().remoteKeysByPhotoId(id) }
            }
        }
    }
}
