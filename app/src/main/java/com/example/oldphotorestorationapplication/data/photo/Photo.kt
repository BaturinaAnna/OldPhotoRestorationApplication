package com.example.oldphotorestorationapplication.data.photo

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo")
data class Photo(
    @ColumnInfo(name = "initialPhoto") val initialPhoto: Bitmap,
    @ColumnInfo(name = "restoredPhoto") val restoredPhoto: Bitmap,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "location") var location: String?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="idPhoto") var idPhoto: Long = 0

    constructor(id: Long,
                initialPhoto: Bitmap,
                restoredPhoto: Bitmap,
                title: String?,
                description: String?,
                date: String?,
                location: String?):
            this(initialPhoto = initialPhoto,
                restoredPhoto = restoredPhoto,
                title = title,
                description = description,
                date = date,
                location = location){
        idPhoto = id
    }
}

