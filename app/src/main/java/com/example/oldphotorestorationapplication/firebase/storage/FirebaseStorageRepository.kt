package com.example.oldphotorestorationapplication.firebase.storage

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*

class FirebaseStorageRepository {
    private val firebaseStorageReference = FirebaseStorage.getInstance().reference

    suspend fun addPhotoToUser(userId: String, initialPhoto: ByteArray, restoredPhoto: ByteArray):
            Triple<Long, FirebaseStorageResult<Uri?>, FirebaseStorageResult<Uri?>> {
        val idPhoto = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE

        val photoReference = firebaseStorageReference.child("$userId/photos/${idPhoto}")

        val addingInitialPhotoResult: FirebaseStorageResult<Uri?> =
            addImage(initialPhoto, photoReference.child("initial.png"))
        val addingRestoredPhotoResult: FirebaseStorageResult<Uri?> =
            addImage(restoredPhoto, photoReference.child("restored.png"))

        return Triple(idPhoto, addingInitialPhotoResult, addingRestoredPhotoResult)
    }

    suspend fun addFaceForPhoto(userId: String, idPhoto: Long, face: ByteArray): Pair<Long, FirebaseStorageResult<Uri?>> {
        val idFace = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
        val facesReferences = firebaseStorageReference.child("$userId/photos/$idPhoto/$idFace")
        return Pair(idFace, addImage(face, facesReferences))
    }

    private suspend fun addImage(image: ByteArray, pathReference: StorageReference): FirebaseStorageResult<Uri?> {
        Log.d("ANNA", pathReference.path)
        var addingImageResult: FirebaseStorageResult<Uri?> = FirebaseStorageResult.Error(null)
        try{
            pathReference
                .putBytes(image)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    } else {
                        pathReference.downloadUrl
                    }
                }.addOnCompleteListener { task ->
                    addingImageResult = if (task.isSuccessful) {
                        FirebaseStorageResult.Success(task.result)
                    } else {
                        FirebaseStorageResult.Error(task.exception?.message)
                    }
                }.await()
            } catch (e: Exception){
                addingImageResult =FirebaseStorageResult.Error(e.message)
            }
        return addingImageResult
    }
}
