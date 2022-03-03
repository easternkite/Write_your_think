package com.multimedia.writeyourthink.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.multimedia.writeyourthink.models.Diary

@Database(
    entities = [Diary::class],
    version = 1
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun getDiaryDao(): DiaryDao

    companion object {
        private var instance: DiaryDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                DiaryDatabase::class.java,
                "write_your_think_db.db"
            ).build()
    }

}