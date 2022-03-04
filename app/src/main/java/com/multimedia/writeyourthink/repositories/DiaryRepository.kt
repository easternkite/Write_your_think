package com.multimedia.writeyourthink.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

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
        count: MutableLiveData<HashMap<String, Int>>
    ) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val diaryList = mutableListOf<Diary>()
                val itemCount = HashMap<String, Int>()
                for (snapshot in snapshot.children) {
                    val diary = snapshot.getValue(Diary::class.java)
                    diaryList.add(diary!!)

                    if (!diary.date.isNullOrEmpty()) {
                        val dateYMD = diary.date.substring(0, 10)
                        if (!itemCount.containsKey(dateYMD)) {
                            itemCount[dateYMD] = 1
                        }
                        else {
                            itemCount[dateYMD] = itemCount[dateYMD]!!.plus(1)
                        }
                    }

                }
                mutableLiveData.value = diaryList
                count.value = itemCount
                Log.d("Lee", "Data Changed ${count.value}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("firebase", databaseError.toException().toString()) // 에러문 출력
            }
        })
        return
    }
}