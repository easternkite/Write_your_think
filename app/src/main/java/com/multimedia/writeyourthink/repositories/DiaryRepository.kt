package com.multimedia.writeyourthink.repositories

import com.google.firebase.database.DatabaseReference
import com.multimedia.writeyourthink.db.DiaryDatabase
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo

class DiaryRepository(
    val databaseReference: DatabaseReference,
    val db: DiaryDatabase
) {


    fun getSavedDiary() = db.getDiaryDao().getAllDiaryData()
    suspend fun upsert(diary: Diary) = db.getDiaryDao().upsert(diary)
    suspend fun deleteDiary(diary: Diary) = db.getDiaryDao().deleteDiary(diary)


    fun writeNewUser(
        userInfo: UserInfo
    ) {
        databaseReference.child("UserInfo").setValue(userInfo)
    }

    fun writeNewDiary(
        diary: Diary
    ) {
        databaseReference.child((diary.date)!!).setValue(diary)
    }
}