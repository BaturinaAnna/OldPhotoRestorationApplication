package com.example.oldphotorestorationapplication.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPhoto(photo:Photo)

    @Query("SELECT * FROM photo")
    fun readAllData(): LiveData<List<Photo>>
}