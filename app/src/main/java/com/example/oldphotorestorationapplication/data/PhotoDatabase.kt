package com.example.oldphotorestorationapplication.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.oldphotorestorationapplication.App
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.face.FaceDao
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.data.photo.PhotoDao
import com.example.oldphotorestorationapplication.data.photowithfaces.PhotoWithFacesDao

@Database(entities = [Photo::class, Face::class], version = 9)
@TypeConverters(Converters::class)
abstract class PhotoDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun faceDao(): FaceDao
    abstract fun photoAndFacesDao(): PhotoWithFacesDao
}
