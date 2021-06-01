package com.example.oldphotorestorationapplication.data

import androidx.lifecycle.LiveData

class PhotoRepository(private val photoDao: PhotoDao, private val faceDao: FaceDao) {
    val readAllData: LiveData<List<Photo>> = photoDao.readAllData()

    suspend fun addPhoto(photo: Photo) {
        photoDao.addPhoto(photo)
    }

    suspend fun addPhotoWithFaces(photo: Photo, faces: List<Face>){
        val idInsertedPhoto = photoDao.addPhoto(photo)
        for(face in faces){
            face.idPhoto = idInsertedPhoto
            faceDao.addFace(face)
        }
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
        return faceDao.findFacesByPhotoId(idPhoto)
    }
}
