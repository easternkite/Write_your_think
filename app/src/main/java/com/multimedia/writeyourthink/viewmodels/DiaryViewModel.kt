package com.multimedia.writeyourthink.viewmodels

import androidx.lifecycle.ViewModel
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo
import com.multimedia.writeyourthink.repositories.DiaryRepository

class DiaryViewModel(
    val diaryRepository: DiaryRepository
) : ViewModel() {


    fun saveUser(userInfo: UserInfo) {
        diaryRepository.writeNewUser(userInfo)
    }
    fun saveDiary(diary: Diary) {
        diaryRepository.writeNewDiary(diary)
    }
}