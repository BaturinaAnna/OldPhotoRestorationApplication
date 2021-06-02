package com.example.oldphotorestorationapplication.data

import android.util.Log
import androidx.lifecycle.LiveData

class PhotoRepository(private val photoDao: PhotoDao, private val faceDao: FaceDao) {
    val readAllPhoto: LiveData<List<Photo>> = photoDao.readAllData()
    val readAllFaces: LiveData<List<Face>> = faceDao.readAllFaces()

    suspend fun addPhoto(photo: Photo):Long {
        return photoDao.addPhoto(photo)
    }

    suspend fun addFace(face: Face) {
        faceDao.addFace(face)
    }

    suspend fun updatePhoto(photo: Photo) {
        photoDao.updatePhoto(photo)
    }

    suspend fun deletePhoto(photo: Photo) {
        photoDao.deletePhoto(photo)
    }

    fun findPhotoById(id: Long): LiveData<Photo> {
        return photoDao.findPhotoById(id = id)
    }

    fun findFacesByPhotoId(idPhoto: Long): LiveData<List<Face>> {
        return faceDao.findFacesByPhotoId(idPhoto = idPhoto)
    }

    fun findFaceById(id: Long): LiveData<Face> {
        return faceDao.findFaceById(id = id)
    }

    suspend fun updateFace(face: Face) {
        faceDao.updateFace(face)
    }
}
