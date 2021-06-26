package com.example.oldphotorestorationapplication.firebase.realtimedatabase

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.oldphotorestorationapplication.App
import com.example.oldphotorestorationapplication.data.firebase.photo.PhotoFirebase
import com.example.oldphotorestorationapplication.getBitmapFromURL
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

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

    fun generateNextId(idUser: String): String? {
        return firebaseRealtimeDatabase.child("users/$idUser/photos").push().key
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

    fun setPhotoInfo(idUser: String, idPhoto: String, infoType: String, info: String){
        firebaseRealtimeDatabase
            .child("users/$idUser/photos/$idPhoto/$infoType")
            .setValue(info)
    }

    fun getPhotoById(idUser: String, idPhoto: String): LiveData<PhotoFirebase> {
        val photo: MutableLiveData<PhotoFirebase> = MutableLiveData()
        firebaseRealtimeDatabase
            .child("users/$idUser/photos/")
            .orderByKey()
            .startAt(idPhoto)
            .limitToFirst(1)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                            val ph = toPhotos(dataSnapshot)
                            Log.d("ANNA", "IN GET PHOTO BY ID $ph")
                            if (ph.isNotEmpty()){
                                photo.postValue(toPhotos(dataSnapshot)[0])
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("firebase", databaseError.toException())
                    }
                }
            )
        return photo
    }


//    fun getPhotos(idUser: String, startPhotoId: String, limit: Int): LiveData<List<PhotoFirebase>> {
//        val allPhoto: MutableLiveData<List<PhotoFirebase>> = MutableLiveData()
//        firebaseRealtimeDatabase
//            .child("users/$idUser/photos")
//            .startAt(startPhotoId)
//            .limitToFirst(limit)
//            .addValueEventListener(
//                object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
//                            allPhoto.postValue(toPhotos(dataSnapshot))
//                        }
//                    }
//                    override fun onCancelled(databaseError: DatabaseError) {
//                        Log.w("firebase", databaseError.toException())
//                    }
//                }
//            )
//        return allPhoto
//    }

    suspend fun getPhotos(idUser: String, startPhotoId: String, limit: Int): List<PhotoFirebase> {
        var allPhoto: List<PhotoFirebase> = ArrayList()
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val dataSnapshot = firebaseRealtimeDatabase
                .child("users/$idUser/photos")
                .orderByKey()
                .startAt(startPhotoId)
                .limitToFirst(limit)
                .get()
                .await()
            allPhoto = toPhotos(dataSnapshot)
        }.join()
        return allPhoto
    }

    suspend fun getFirstPhotoId(idUser: String): String? {
        var id: String? = null
        CoroutineScope(Dispatchers.IO + SupervisorJob())
            .launch {
                val dataSnapshot =
                    firebaseRealtimeDatabase
                        .child("users/$idUser/photos")
                        .orderByKey()
                        .limitToFirst(1)
                        .get()
                        .await()
                val list = dataSnapshot.children.map {  it.key.toString()}
                if (list.isNotEmpty()){
                    id = list[0]
                }
            }
            .join()
        return id
    }

    suspend fun getNextPhotoId(idUser: String, currentPhotoId: String): String {
        var id = ""
        CoroutineScope(Dispatchers.IO + SupervisorJob())
            .launch {
                val dataSnapshot =
                    firebaseRealtimeDatabase
                        .child("users/$idUser/photos")
                        .orderByKey()
                        .startAfter(currentPhotoId)
                        .limitToFirst(1)
                        .get()
                        .await()
                id = dataSnapshot.children.map {  it.key.toString()}[0]
            }
            .join()
        return id
    }

    suspend fun getPrevPhotoId(idUser: String, currentPhotoId: String): String {
        var id = ""
        CoroutineScope(Dispatchers.IO + SupervisorJob())
            .launch {
                val dataSnapshot =
                    firebaseRealtimeDatabase
                        .child("users/$idUser/photos")
                        .orderByKey()
                        .endBefore(currentPhotoId)
                        .limitToLast(1)
                        .get()
                        .await()
                id = dataSnapshot.children.map {  it.key.toString()}[0]
            }
            .join()
        return id
    }

    private fun toPhotos(dataSnapshot: DataSnapshot): List<PhotoFirebase> {
        return dataSnapshot.children.map {
            val idPhoto = it.key
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
                initialPhoto = getBitmapFromURL(initial),
                restoredPhoto = getBitmapFromURL(restored),
                title = null,
                description = null,
                date = null,
                location = null
            )


        }
    }
}
