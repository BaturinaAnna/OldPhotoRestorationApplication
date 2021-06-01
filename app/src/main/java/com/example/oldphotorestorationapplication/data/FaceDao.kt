package com.example.oldphotorestorationapplication.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FaceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFace(face: Face)

    @Query("SELECT * FROM face WHERE idPhoto = :idPhoto")
    fun findFacesByPhotoId(idPhoto: Long): LiveData<List<Face>>
}