package com.example.oldphotorestorationapplication.data

import androidx.lifecycle.LiveData

class PhotoRepository(private val photoDao: PhotoDao) {
    val readAllData: LiveData<List<Photo>> = photoDao.readAllData()

   suspend fun addPhoto(photo:Photo){
        photoDao.addPhoto(photo)
    }
}