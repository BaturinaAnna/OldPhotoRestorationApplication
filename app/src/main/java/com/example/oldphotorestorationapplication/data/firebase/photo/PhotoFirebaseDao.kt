package com.example.oldphotorestorationapplication.data.firebase.photo

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.example.oldphotorestorationapplication.data.photo.Photo

@Dao
interface PhotoFirebaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoList(news: List<PhotoFirebase>)

    @Query("SELECT * FROM PhotoFirebase")
    fun observePhotoPaginated(): PagingSource<Int, PhotoFirebase>

    @Query("DELETE FROM PhotoFirebase")
    fun deletePhotoItems(): Int
}