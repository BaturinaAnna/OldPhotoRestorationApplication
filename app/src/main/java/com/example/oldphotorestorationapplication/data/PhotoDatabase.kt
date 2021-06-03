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

@Database(entities = [Photo::class, Face::class], version = 8)
@TypeConverters(Converters::class)
abstract class PhotoDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun faceDao(): FaceDao

    companion object {
        @Volatile private var INSTANCE: PhotoDatabase? = null

        fun getDatabase(): PhotoDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                            App.appContext,
                            PhotoDatabase::class.java,
                            "OldPhotoDatabase"
                        )
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
