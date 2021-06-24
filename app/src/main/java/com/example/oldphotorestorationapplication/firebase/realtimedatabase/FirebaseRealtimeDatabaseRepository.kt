package com.example.oldphotorestorationapplication.firebase.realtimedatabase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.data.photo.PhotoFirebase
import com.example.oldphotorestorationapplication.getBitmapFromURL
import com.google.firebase.database.*
import kotlinx.coroutines.*

class FirebaseRealtimeDatabaseRepository {

    private var firebaseRealtimeDatabase: DatabaseReference =
        FirebaseDatabase.getInstance(
                "https://oldphotorestorationapplication-default-rtdb.europe-west1.firebasedatabase.app/"
            )
            .reference

    fun addPhotoToUser(
        idUser: String,
        idPhoto: String,
        photoInitialUri: String,
        photoRestoredUri: String
    ) {
        firebaseRealtimeDatabase
            .child("users/$idUser/photos/$idPhoto/initial")
            .setValue(photoInitialUri)
        firebaseRealtimeDatabase
            .child("users/$idUser/photos/$idPhoto/restored")
            .setValue(photoRestoredUri)
    }

    fun generateNextId(idUser: String): String?{
        return firebaseRealtimeDatabase
            .child("users/$idUser/photos")
            .push()
            .key
    }

    fun addFaceToPhoto(idUser: String, idPhoto: String, idFace: Long, faceUri: String) {
        firebaseRealtimeDatabase
            .child("users/$idUser/photos/$idPhoto/faces/$idFace")
            .setValue(faceUri)
    }

    fun getAllPhotos(idUser: String): LiveData<List<PhotoFirebase>> {
        val allPhoto: MutableLiveData<List<PhotoFirebase>> = MutableLiveData()
        firebaseRealtimeDatabase
            .child("users/$idUser/photos")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                            allPhoto.postValue(toPhotos(dataSnapshot))
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("firebase", databaseError.toException())
                    }
                }
            )
        return allPhoto
    }


    fun getNextPhoto(idUser: String, currentPhotoId: String): LiveData<PhotoFirebase>{
        val nextPhotoId: MutableLiveData<PhotoFirebase> = MutableLiveData()
        firebaseRealtimeDatabase
            .child("users/$idUser/photos")
            .orderByKey()
            .startAfter(currentPhotoId)
            .limitToFirst(1)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                            nextPhotoId.postValue(toPhotos(dataSnapshot)[0])
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("firebase", databaseError.toException())
                    }
                }
            )
        return nextPhotoId
    }

    fun getPrevPhoto(idUser: String, currentPhotoId: String): LiveData<PhotoFirebase>{
        val nextPhotoId: MutableLiveData<PhotoFirebase> = MutableLiveData()
        firebaseRealtimeDatabase
            .child("users/$idUser/photos")
            .orderByKey()
            .endBefore(currentPhotoId)
            .limitToLast(1)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                            nextPhotoId.postValue(toPhotos(dataSnapshot)[0])
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("firebase", databaseError.toException())
                    }
                }
            )
        return nextPhotoId
    }


    private fun toPhotos(dataSnapshot: DataSnapshot): List<PhotoFirebase> {
        return dataSnapshot.children.map {
            val idPhoto= it.key
            var initial = ""
            var restored = ""

            it.children.forEach {
                when (it.key) {
                    "initial" -> initial = it.value as String
                    "restored" -> restored = it.value as String
                }
            }
            PhotoFirebase(
                idPhoto = idPhoto!!,
                initialPhoto = getBitmapFromURL(initial)!!,
                restoredPhoto = getBitmapFromURL(restored)!!,
                title = null,
                description = null,
                date = null,
                location = null
            )
        }
    }
}
