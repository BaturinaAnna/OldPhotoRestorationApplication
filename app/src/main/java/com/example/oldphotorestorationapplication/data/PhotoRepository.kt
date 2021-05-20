package com.example.oldphotorestorationapplication.data

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope

class PhotoRepository(private val photoDao: PhotoDao) {
  val readAllData: LiveData<List<Photo>> = photoDao.readAllData()

  suspend fun addPhoto(photo: Photo) {
    photoDao.addPhoto(photo)
  }

  suspend fun updatePhoto(photo: Photo) {
    photoDao.updatePhoto(photo)
  }

  suspend fun deletePhoto(photo: Photo) {
    photoDao.deletePhoto(photo)
  }

  fun findPhotoById(id: Int): LiveData<Photo> {
    return photoDao.findPhotoById(id = id)
  }
}
