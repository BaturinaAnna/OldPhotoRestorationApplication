package com.example.oldphotorestorationapplication.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPhoto(photo:Photo)

    @Update
    suspend fun updatePhoto(photo: Photo)

    @Delete
    suspend fun deletePhoto(photo: Photo)

    @Query("SELECT * FROM photo")
    fun readAllData(): LiveData<List<Photo>>

    @Query("SELECT * FROM photo WHERE id = :id")
    fun findPhotoById(id: Int): LiveData<Photo>
}