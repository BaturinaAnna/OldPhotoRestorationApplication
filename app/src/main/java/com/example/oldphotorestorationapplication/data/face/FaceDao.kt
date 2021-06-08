package com.example.oldphotorestorationapplication.data.face

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.oldphotorestorationapplication.data.face.Face

@Dao
interface FaceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFace(face: Face)

    @Update
    suspend fun updateFace(face: Face)

    @Query("SELECT * FROM face WHERE idFace= :id")
    fun findFaceById(id: Long): LiveData<Face>

    @Query("SELECT * FROM face")
    fun readAllFaces(): LiveData<List<Face>>

    @Query("SELECT * FROM face WHERE face.name IS NOT NULL")
    fun readAllFacesWithNames(): LiveData<List<Face>>
}