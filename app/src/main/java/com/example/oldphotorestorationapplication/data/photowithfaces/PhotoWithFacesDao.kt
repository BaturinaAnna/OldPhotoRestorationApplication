package com.example.oldphotorestorationapplication.data.photowithfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo

@Dao
interface PhotoWithFacesDao {
    @Query("SELECT * FROM face WHERE idPhotoFace = :idPhoto")
    fun findFacesByPhotoId(idPhoto: Long): LiveData<List<Face>>

    @Transaction
    @Query("SELECT * FROM photo")
    fun readAllPhotoWithFaces(): LiveData<List<PhotoWithFaces>>
}