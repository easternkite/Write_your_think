package com.multimedia.writeyourthink.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo

class DiaryRepository(
    val databaseReference: DatabaseReference,
) {


    fun writeNewUserToFirebase(userInfo: UserInfo) {
        databaseReference.child("UserInfo").setValue(userInfo)
    }

    fun writeNewDiaryToFirebase(diary: Diary) {
        databaseReference.child(diary.date).setValue(diary)
    }

    fun deleteFromFirebase(diary: Diary) {
        databaseReference.child(diary.date).setValue(null)
    }

    fun getFirebaseData(
        mutableLiveData: MutableLiveData<MutableList<Diary>>,
        selectedDateTime: MutableLiveData<String>
    ) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val diaryList = mutableListOf<Diary>()
                for (snapshot in snapshot.children) {
                    val diary = snapshot.getValue(Diary::class.java)
                    diaryList.add(diary!!)

                }
                mutableLiveData.value = diaryList
                Log.d("Lee", "Data Changed")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("firebase", databaseError.toException().toString()) // 에러문 출력
            }
        })
        return
    }


}