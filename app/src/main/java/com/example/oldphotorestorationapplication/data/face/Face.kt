package com.example.oldphotorestorationapplication.data.face

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.example.oldphotorestorationapplication.data.photo.Photo

@Entity(tableName = "face",
        foreignKeys = [
            ForeignKey(entity = Photo::class,
                parentColumns = ["idPhoto"],
                childColumns = ["idPhotoFace"],
                onDelete = CASCADE)])
data class Face(
    @ColumnInfo(name = "face") val face: Bitmap,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "idPhotoFace") var idPhotoFace: Long?
) {
    @PrimaryKey(autoGenerate = true) var idFace: Long = 0
}