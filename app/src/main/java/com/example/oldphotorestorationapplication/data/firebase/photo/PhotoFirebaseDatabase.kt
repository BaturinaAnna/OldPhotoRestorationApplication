package com.example.oldphotorestorationapplication.data.firebase.photo

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.oldphotorestorationapplication.App
import com.example.oldphotorestorationapplication.data.Converters
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.face.FaceDao
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.data.photo.PhotoDao
import com.example.oldphotorestorationapplication.data.photowithfaces.PhotoWithFacesDao

@Database(entities = [PhotoFirebase::class, PhotoItemRemoteKeys::class], version = 3)
@TypeConverters(Converters::class)
abstract class PhotoFirebaseDatabase : RoomDatabase() {
    abstract fun photoFirebaseDao(): PhotoFirebaseDao
    abstract fun photoItemRemoteKeysDao(): PhotoItemRemoteKeysDao

    companion object {
        @Volatile private var INSTANCE: PhotoFirebaseDatabase? = null

        fun getDatabase(): PhotoFirebaseDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        App.appContext,
                        PhotoFirebaseDatabase::class.java,
                        "db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}