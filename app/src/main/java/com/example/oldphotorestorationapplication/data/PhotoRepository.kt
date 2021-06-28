package com.example.oldphotorestorationapplication.data

import androidx.lifecycle.LiveData
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.face.FaceDao
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.data.photo.PhotoDao
import com.example.oldphotorestorationapplication.data.photowithfaces.PhotoWithFaces
import com.example.oldphotorestorationapplication.data.photowithfaces.PhotoWithFacesDao
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val photoDao: PhotoDao,
    private val faceDao: FaceDao,
    private val photoWithFacesDao: PhotoWithFacesDao
) {
    val readAllPhoto: LiveData<List<Photo>> = photoDao.readAllData()
    val readAllFaces: LiveData<List<Face>> = faceDao.readAllFaces()
    val readAllPhotoWithFaces: LiveData<List<PhotoWithFaces>> = photoWithFacesDao.readAllPhotoWithFaces()
    val readAllFacesWithNames: LiveData<List<Face>> = faceDao.readAllFacesWithNames()

    suspend fun addPhoto(photo: Photo): Long {
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
        return photoWithFacesDao.findFacesByPhotoId(idPhoto = idPhoto)
    }

    fun findFaceById(id: Long): LiveData<Face> {
        return faceDao.findFaceById(id = id)
    }

    suspend fun updateFace(face: Face) {
        faceDao.updateFace(face)
    }
}
