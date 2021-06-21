package com.example.oldphotorestorationapplication.firebase.realtimedatabase

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.toByteArray
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class FirebaseRealtimeDatabaseRepository {

    private var firebaseRealtimeDatabase: DatabaseReference = FirebaseDatabase
        .getInstance("https://oldphotorestorationapplication-default-rtdb.europe-west1.firebasedatabase.app/")
        .reference


    fun addPhotoToUser(idUser: String, idPhoto: Long, photoInitialUri: String, photoRestoredUri: String){
        firebaseRealtimeDatabase
            .child("users/$idUser/photos/$idPhoto/initial")
            .setValue(photoInitialUri)
        firebaseRealtimeDatabase
            .child("users/$idUser/photos/$idPhoto/restored")
            .setValue(photoRestoredUri)
    }

    fun addFaceToPhoto(idUser: String, idPhoto: Long, idFace: Long, faceUri: String){
        firebaseRealtimeDatabase
            .child("users/$idUser/photos/$idPhoto/faces/$idFace")
            .setValue(faceUri)
    }

//    fun addPhotoToUser(initialPhoto: Bitmap, restoredPhoto: Bitmap, faces: List<ByteArray>?) {
//
//
//        val idPhoto = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
//        val idFaces = ArrayList<Long>()
//
//
//        val photoReference =
//            firebaseStorageReference
//                .child("${FirebaseAuth.getInstance().currentUser!!.uid}/photos/${idPhoto}")
//
//
//        photoReference.child("initial.png").putBytes(initialPhoto.toByteArray())
//
//        photoReference.child("restored.png").putBytes(restoredPhoto.toByteArray())
//
//        val facesReferences = photoReference.child("faces")
//
//        faces?.let {
//            for (face in faces) {
//                    val idFace = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
//                    idFaces.add(idFace)
//                    facesReferences.child("$idFace").putBytes(face)
//                }
//            }
//
//        //        firebaseRealtimeDatabaseReference.child("users")
//        //            .child(userId)
//        //    ]}}        .child("photos")
//        //            .child("photoId")
//        //            .setValue(photo)
//    }
}
