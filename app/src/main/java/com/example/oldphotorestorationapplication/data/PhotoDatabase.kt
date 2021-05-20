package com.example.oldphotorestorationapplication.data

import android.content.Context
import androidx.room.*
import com.example.oldphotorestorationapplication.App

@Database(entities = [Photo::class], version=5)
@TypeConverters(Converters::class)
abstract class PhotoDatabase: RoomDatabase() {
    abstract fun photoDao(): PhotoDao

    companion object{
        @Volatile
        private var INSTANCE: PhotoDatabase? = null

        fun getDatabase(): PhotoDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    App.appContext,
                    PhotoDatabase::class.java,
                    "OldPhotoDatabase"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}