package com.multimedia.writeyourthink.viewmodels

import androidx.lifecycle.ViewModel
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo
import com.multimedia.writeyourthink.repositories.FirebaseRepository

class DiaryViewModel(
    val firebaseRepository: FirebaseRepository
) : ViewModel() {


    fun saveUser(userInfo: UserInfo) {
        firebaseRepository.writeNewUser(userInfo)
    }
    fun saveDiary(diary: Diary) {
        firebaseRepository.writeNewDiary(diary)
    }
}