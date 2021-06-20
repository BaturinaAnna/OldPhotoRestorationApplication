package com.example.oldphotorestorationapplication.peoplegallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.oldphotorestorationapplication.data.PhotoDatabase
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.face.Face

class PeopleViewModel(application: Application) : AndroidViewModel(application) {
        val allFacesWithNames: LiveData<List<Face>>
        private val repository: PhotoRepository

        init {
            val photoDao = PhotoDatabase.getDatabase().photoDao()
            val faceDao = PhotoDatabase.getDatabase().faceDao()
            val photoAndFacesDao = PhotoDatabase.getDatabase().photoAndFacesDao()
            repository = PhotoRepository(photoDao, faceDao, photoAndFacesDao)
            allFacesWithNames = repository.readAllFacesWithNames
        }
}