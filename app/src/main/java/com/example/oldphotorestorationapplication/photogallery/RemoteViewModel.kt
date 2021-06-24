package com.example.oldphotorestorationapplication.photogallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.liveData
import com.example.oldphotorestorationapplication.mediator.RemoteRepository

class RemoteViewModel(application: Application) : AndroidViewModel(application)  {
    private val remoteRepository = RemoteRepository()

    @ExperimentalPagingApi
    val photosPaginated = remoteRepository.observePhotoListPaginated().liveData

}