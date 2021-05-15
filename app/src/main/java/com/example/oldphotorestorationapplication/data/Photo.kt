package com.example.oldphotorestorationapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo")
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "initialPhoto", typeAffinity = ColumnInfo.BLOB) val initialPhoto: ByteArray,
    @ColumnInfo(name = "restoredPhoto", typeAffinity = ColumnInfo.BLOB) val restoredPhoto: ByteArray,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "location") val location: String?
)

//class Photo(var id:Long?,
//            var initialPhoto: ByteArray,
//            var restoredPhoto: ByteArray,
//            var title: String?,
//            var description: String?,
//            var date: String?,
//            var location: String?)