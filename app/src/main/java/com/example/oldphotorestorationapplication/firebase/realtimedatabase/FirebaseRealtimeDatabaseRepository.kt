package com.example.oldphotorestorationapplication.firebase.realtimedatabase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.oldphotorestorationapplication.data.photo.Photo
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
        idPhoto: Long,
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

    fun addFaceToPhoto(idUser: String, idPhoto: Long, idFace: Long, faceUri: String) {
        firebaseRealtimeDatabase
            .child("users/$idUser/photos/$idPhoto/faces/$idFace")
            .setValue(faceUri)
    }

    fun getAllPhotos(idUser: String): LiveData<List<Photo>> {
        val allPhoto: MutableLiveData<List<Photo>> = MutableLiveData()
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

    private fun toPhotos(dataSnapshot: DataSnapshot): List<Photo> {
        return dataSnapshot.children.map {
            val idPhoto: Long? = it.key?.toLong()
            var initial = ""
            var restored = ""
            it.children.forEach {
                when (it.key) {
                    "initial" -> initial = it.value as String
                    "restored" -> restored = it.value as String
                }
            }
            Photo(
                id = idPhoto!!,
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
