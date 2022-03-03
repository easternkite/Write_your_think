package com.multimedia.writeyourthink.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.multimedia.writeyourthink.models.Diary

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(diary: Diary): Long

    @Query("SELECT * FROM diaries")
    fun getAllDiaryData() : LiveData<List<Diary>>

    @Query("SELECT * FROM diaries WHERE date = :date")
    suspend fun getDiaryByDate(date: String) : LiveData<List<Diary>>

    @Delete
    suspend fun deleteDiary(diary: Diary)
}