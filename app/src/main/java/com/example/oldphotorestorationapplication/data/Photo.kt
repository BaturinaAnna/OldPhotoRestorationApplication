package com.example.oldphotorestorationapplication.data

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo")
data class Photo(
//    @ColumnInfo(name = "initialPhoto", typeAffinity = ColumnInfo.BLOB) val initialPhoto: ByteArray,
    @ColumnInfo(name = "initialPhoto") val initialPhoto: Bitmap,
//    @ColumnInfo(name = "restoredPhoto", typeAffinity = ColumnInfo.BLOB) val restoredPhoto: ByteArray,
    @ColumnInfo(name = "restoredPhoto") val restoredPhoto: Bitmap,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "location") val location: String?
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}