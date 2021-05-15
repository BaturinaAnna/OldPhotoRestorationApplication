package com.example.oldphotorestorationapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Photo::class], version=1)
abstract class PhotoDatabase: RoomDatabase() {
    abstract fun photoDao(): PhotoDao

    companion object{
        private var INSTANCE: PhotoDatabase? = null

        fun getDatabase(context: Context): PhotoDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhotoDatabase::class.java,
                    "OldPhotoDatabase"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}