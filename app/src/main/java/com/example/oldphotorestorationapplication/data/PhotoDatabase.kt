package com.example.oldphotorestorationapplication.data

import android.content.Context
import androidx.room.*

@Database(entities = [Photo::class], version=5)
@TypeConverters(Converters::class)
abstract class PhotoDatabase: RoomDatabase() {
    abstract fun photoDao(): PhotoDao

    companion object{
        @Volatile
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
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}