package com.multimedia.writeyourthink.repositories

import com.google.firebase.database.DatabaseReference
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo

class FirebaseRepository(
    val databaseReference: DatabaseReference
) {



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