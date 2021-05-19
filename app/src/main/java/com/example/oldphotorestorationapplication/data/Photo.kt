package com.example.oldphotorestorationapplication.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
//import com.example.oldphotorestorationapplication.convertToByteArray
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


@Parcelize
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
): Parcelable{
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}