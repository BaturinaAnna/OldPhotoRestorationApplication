package com.example.oldphotorestorationapplication.data.firebase.photo

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhotoFirebase(
    @PrimaryKey @ColumnInfo(name = "idPhoto") var idPhoto: String,
    @ColumnInfo(name = "initialPhoto") val initialPhoto: Bitmap?,
    @ColumnInfo(name = "restoredPhoto") val restoredPhoto: Bitmap?,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "location") var location: String?
)