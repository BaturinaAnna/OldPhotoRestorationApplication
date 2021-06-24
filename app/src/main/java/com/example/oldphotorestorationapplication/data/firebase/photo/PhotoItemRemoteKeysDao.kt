package com.example.oldphotorestorationapplication.data.firebase.photo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoItemRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remoteKey: List<PhotoItemRemoteKeys>)

    @Query("SELECT * FROM PhotoItemRemoteKeys WHERE photoId = :photoId")
    fun remoteKeysByPhotoId(photoId: String): PhotoItemRemoteKeys?

    @Query("DELETE FROM PhotoItemRemoteKeys")
    fun clearRemoteKeys()
}