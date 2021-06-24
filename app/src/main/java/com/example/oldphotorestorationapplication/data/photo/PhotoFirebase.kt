package com.example.oldphotorestorationapplication.data.photo

import android.graphics.Bitmap


data class PhotoFirebase(
    val initialPhoto: Bitmap,
    val restoredPhoto: Bitmap,
    var title: String?,
    var description: String?,
    var date: String?,
    var location: String?,
    var idPhoto: String
)