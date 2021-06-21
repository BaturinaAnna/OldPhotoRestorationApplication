package com.example.oldphotorestorationapplication.photorestorationsettings

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.oldphotorestorationapplication.authentication.AuthenticationActivity
import com.example.oldphotorestorationapplication.data.PhotoDatabase
import com.example.oldphotorestorationapplication.data.PhotoRepository
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.firebase.firebaseauth.AuthResult
import com.example.oldphotorestorationapplication.firebase.firebaseauth.FirebaseAuthRepository
import com.example.oldphotorestorationapplication.firebase.realtimedatabase.FirebaseRealtimeDatabaseRepository
import com.example.oldphotorestorationapplication.firebase.storage.FirebaseStorageRepository
import com.example.oldphotorestorationapplication.firebase.storage.FirebaseStorageResult
import com.example.oldphotorestorationapplication.network.NetworkRepository
import com.example.oldphotorestorationapplication.network.RestorationNetwork
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.io.path.Path
import kotlin.io.path.readBytes

class PhotoRestorationSettingsViewModel(application: Application) : AndroidViewModel(application) {
    // FOR FIREBASE
    private val repositoryNetwork: NetworkRepository
    private val firebaseStorageRepository: FirebaseStorageRepository
    private val firebaseAuthRepository: FirebaseAuthRepository
    private val firebaseRealtimeDatabaseRepository: FirebaseRealtimeDatabaseRepository

    init {
        val restorationNetwork = RestorationNetwork()
        repositoryNetwork = NetworkRepository(restorationNetwork)
        firebaseStorageRepository = FirebaseStorageRepository()
        firebaseAuthRepository = FirebaseAuthRepository()
        firebaseRealtimeDatabaseRepository = FirebaseRealtimeDatabaseRepository()
    }

    fun restoreAndSavePhoto(imagePath: String, removeScratches: String) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val restoredPhotoList = repositoryNetwork.restorePhoto(imagePath, removeScratches)
            val idUser = firebaseAuthRepository.getCurrentUserId()!!
            val addingPhotoResult = firebaseStorageRepository.addPhotoToUser(
                userId = idUser,
                initialPhoto = Path(imagePath).readBytes(),
                restoredPhoto = restoredPhotoList[0])

            if (addingPhotoResult.second is FirebaseStorageResult.Success &&
                    addingPhotoResult.third is FirebaseStorageResult.Success ){
                firebaseRealtimeDatabaseRepository.addPhotoToUser(
                    idUser = idUser,
                    idPhoto = addingPhotoResult.first,
                    photoInitialUri = (addingPhotoResult.second as FirebaseStorageResult.Success<Uri?>).data!!.toString(),
                    photoRestoredUri = (addingPhotoResult.third as FirebaseStorageResult.Success<Uri?>).data!!.toString()
                )
                if (restoredPhotoList.size > 1){
                    val addingFacesResults =
                        saveFaces(restoredPhotoList.subList(1, restoredPhotoList.size), idUser, addingPhotoResult.first)
                    if(addingFacesResults.filter { it.second is FirebaseStorageResult.Error }.isEmpty()){
                        for (result in addingFacesResults){
                            firebaseRealtimeDatabaseRepository.addFaceToPhoto(
                                idUser = idUser,
                                idPhoto = addingPhotoResult.first,
                                idFace = result.first,
                                faceUri = result.second.data!!.toString()
                            )
                        }
                    } else {
                        //handle error
                        Log.d("firebase", "FAILED ON ADDING FACES")
                    }
                }
            } else {
                //handle error
                Log.d("firebase", "FAILED ON ADDING PHOTO")
            }
        }
    }

    private suspend fun saveFaces(faces: List<ByteArray>, idUser: String, idPhoto: Long):
            ArrayList<Pair<Long, FirebaseStorageResult<Uri?>>>{
        val addingFacesResults = ArrayList<Pair<Long, FirebaseStorageResult<Uri?>>>()
            for (faceByteArray in faces) {
                addingFacesResults.add(
                    firebaseStorageRepository.addFaceForPhoto(idUser, idPhoto, faceByteArray))
            }
        return addingFacesResults
    }



    // FOR ROOM

//    private val repositoryPhoto: PhotoRepository
//    private val repositoryNetwork: NetworkRepository
//
//    init {
//        val photoDao = PhotoDatabase.getDatabase().photoDao()
//        val faceDao = PhotoDatabase.getDatabase().faceDao()
//        val photoAndFacesDao = PhotoDatabase.getDatabase().photoAndFacesDao()
//        repositoryPhoto = PhotoRepository(photoDao, faceDao, photoAndFacesDao)
//        val restorationNetwork = RestorationNetwork()
//        repositoryNetwork = NetworkRepository(restorationNetwork)
//    }
//
//    fun restoreAndSavePhoto(imagePath: String, removeScratches: String) {
//        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
//            val restoredPhotoList = repositoryNetwork.restorePhoto(imagePath, removeScratches)
//            val bitmapRestoredPhoto = BitmapFactory.decodeByteArray(restoredPhotoList[0], 0, restoredPhotoList[0].size)
//            val bitmapOldPhoto = BitmapFactory.decodeFile(imagePath)
//            val photoToInsert = Photo(bitmapOldPhoto, bitmapRestoredPhoto, null, null, null, null)
//            val idInsertedPhoto = repositoryPhoto.addPhoto(photoToInsert)
//            if (restoredPhotoList.size > 1){
//                for (faceByteArray in restoredPhotoList.subList(1, restoredPhotoList.size)) {
//                    val face = Face(
//                        face = BitmapFactory.decodeByteArray(faceByteArray, 0, faceByteArray.size),
//                        idPhotoFace = idInsertedPhoto,
//                        name = null)
//                    repositoryPhoto.addFace(face)
//                }
//            }
//        }
//    }
}
