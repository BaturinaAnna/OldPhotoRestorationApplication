package com.example.oldphotorestorationapplication.data

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "photo")
data class Photo(
//    @ColumnInfo(name = "initialPhoto", typeAffinity = ColumnInfo.BLOB) val initialPhoto: ByteArray,
    @ColumnInfo(name = "initialPhoto") val initialPhoto: Bitmap,
//    @ColumnInfo(name = "restoredPhoto", typeAffinity = ColumnInfo.BLOB) val restoredPhoto: ByteArray,
    @ColumnInfo(name = "restoredPhoto") val restoredPhoto: Bitmap,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "location") var location: String?
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}