package com.example.oldphotorestorationapplication.peoplegallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.face.Face
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val repositoryPhoto: PhotoRepository
) : ViewModel() {
        val allFacesWithNames: LiveData<List<Face>> = repositoryPhoto.readAllFacesWithNames
}