package com.example.oldphotorestorationapplication.firebase.realtimedatabase

import android.graphics.Bitmap
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.toByteArray
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class FirebaseRealtimeDatabaseRepository {

    private val firebaseRealtimeDatabaseReference =
        FirebaseDatabase.getInstance().getReference("OldPhotoRestorationDB")
    private val firebaseStorageReference = FirebaseStorage.getInstance().reference

    fun addPhotoToUser(initialPhoto: Bitmap, restoredPhoto: Bitmap, faces: List<ByteArray>?) {


        val idPhoto = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
        val idFaces = ArrayList<Long>()


        val photoReference =
            firebaseStorageReference
                .child("${FirebaseAuth.getInstance().currentUser!!.uid}/photos/${idPhoto}")


        photoReference.child("initial.png").putBytes(initialPhoto.toByteArray())

        photoReference.child("restored.png").putBytes(restoredPhoto.toByteArray())

        val facesReferences = photoReference.child("faces")

        faces?.let {
            for (face in faces) {
                    val idFace = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
                    idFaces.add(idFace)
                    facesReferences.child("$idFace").putBytes(face)
                }
            }

        //        firebaseRealtimeDatabaseReference.child("users")
        //            .child(userId)
        //    ]}}        .child("photos")
        //            .child("photoId")
        //            .setValue(photo)
    }
}
