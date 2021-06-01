package com.example.oldphotorestorationapplication.data

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "face",
        foreignKeys = [
            ForeignKey(entity = Photo::class,
                parentColumns = ["id"],
                childColumns = ["idPhoto"],
                onDelete = CASCADE)])
data class Face(
    @ColumnInfo(name = "face") val face: Bitmap,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "idPhoto") var idPhoto: Long?
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}