package com.multimedia.writeyourthink.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo
import javax.inject.Inject
import kotlin.collections.HashMap

class DiaryRepositoryImpl @Inject constructor(
    val databaseReference: DatabaseReference
): DiaryRepository {

    override fun setUserInfo(userInfo: UserInfo) {
        databaseReference.child("UserInfo").setValue(userInfo)
    }

    override fun setDiary(diary: Diary) {
        databaseReference.child(diary.date).setValue(diary)
    }

    override fun getDiaryList(response: (List<Diary>?, HashMap<String,Int>?, Throwable?) -> Unit) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val diaryList = mutableListOf<Diary>()
                val itemCount = HashMap<String, Int>()
                for (snapshot in snapshot.children) {
                    val diary = snapshot.getValue(Diary::class.java)
                    diary?.let { diaryList }
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
                response(diaryList, itemCount, null)
                Log.d("Lee", "Data Changed ${itemCount}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 디비를 가져오던중 에러 발생 시
                response(null, null, databaseError.toException())
                Log.e("firebase", databaseError.toException().toString()) // 에러문 출력
            }
        })
    }

    override fun deleteDiray(diary: Diary) {
        databaseReference.child(diary.date).setValue(null)
    }
}