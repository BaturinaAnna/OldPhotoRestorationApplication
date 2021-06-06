package com.example.oldphotorestorationapplication.data.photo

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.oldphotorestorationapplication.data.photowithfaces.PhotoWithFaces

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPhoto(photo: Photo): Long

    @Update
    suspend fun updatePhoto(photo: Photo)

    @Delete
    suspend fun deletePhoto(photo: Photo)

    @Query("SELECT * FROM photo")
    fun readAllData(): LiveData<List<Photo>>

    @Query("SELECT * FROM photo WHERE idPhoto = :id")
    fun findPhotoById(id: Long): LiveData<Photo>
}
