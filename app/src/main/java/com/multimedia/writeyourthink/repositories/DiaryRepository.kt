package com.multimedia.writeyourthink.repositories

import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo

interface DiaryRepository {
    fun setUserInfo(userInfo: UserInfo)
    fun setDiary(diary: Diary)
    fun getDiaryList(response: (List<Diary>?, HashMap<String, Int>?, Throwable?) -> Unit)
    fun deleteDiray(diary: Diary)

}