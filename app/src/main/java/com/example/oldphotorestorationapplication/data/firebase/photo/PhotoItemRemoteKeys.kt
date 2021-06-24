package com.example.oldphotorestorationapplication.data.firebase.photo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhotoItemRemoteKeys(
    @PrimaryKey
    val photoId: String,
    val prevKey: String?,
    val nextKey: String?
)