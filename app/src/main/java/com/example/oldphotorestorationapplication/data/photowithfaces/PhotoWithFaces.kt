package com.example.oldphotorestorationapplication.data.photowithfaces

import androidx.room.Embedded
import androidx.room.Relation
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo

data class PhotoWithFaces(
    @Embedded val photo: Photo,
    @Relation(
        parentColumn = "idPhoto",
        entityColumn = "idPhotoFace"
    ) val faces: List<Face>
)
