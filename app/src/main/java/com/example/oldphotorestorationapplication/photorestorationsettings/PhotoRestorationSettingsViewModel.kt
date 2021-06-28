package com.example.oldphotorestorationapplication.photorestorationsettings

import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.network.NetworkRepository
import com.example.oldphotorestorationapplication.network.RestorationNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoRestorationSettingsViewModel@Inject constructor(
    private val repositoryPhoto: PhotoRepository,
    private val repositoryNetwork: NetworkRepository
) : ViewModel() {

    fun restoreAndSavePhoto(imagePath: String, removeScratches: String) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val restoredPhotoList = repositoryNetwork.restorePhoto(imagePath, removeScratches)
            val bitmapRestoredPhoto = BitmapFactory.decodeByteArray(restoredPhotoList[0], 0, restoredPhotoList[0].size)
            val bitmapOldPhoto = BitmapFactory.decodeFile(imagePath)
            val photoToInsert = Photo(bitmapOldPhoto, bitmapRestoredPhoto, null, null, null, null)
            val idInsertedPhoto = repositoryPhoto.addPhoto(photoToInsert)
            if (restoredPhotoList.size > 1){
                for (faceByteArray in restoredPhotoList.subList(1, restoredPhotoList.size)) {
                    val face = Face(
                        face = BitmapFactory.decodeByteArray(faceByteArray, 0, faceByteArray.size),
                        idPhotoFace = idInsertedPhoto,
                        name = null)
                    repositoryPhoto.addFace(face)
                }
            }
        }
    }
}
