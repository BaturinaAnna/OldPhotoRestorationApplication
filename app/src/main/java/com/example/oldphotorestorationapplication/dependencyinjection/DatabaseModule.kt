package com.example.oldphotorestorationapplication.dependencyinjection

import android.content.Context
import androidx.room.Room
import com.example.oldphotorestorationapplication.data.PhotoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providePhotoDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, PhotoDatabase::class.java, "OldPhotoDatabase")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun providePhotoDao(database: PhotoDatabase) = database.photoDao()

    @Singleton
    @Provides
    fun provideFaceDao(database: PhotoDatabase) = database.faceDao()

    @Singleton
    @Provides
    fun providePhotoWithDacesDao(database: PhotoDatabase) = database.photoAndFacesDao()
}
