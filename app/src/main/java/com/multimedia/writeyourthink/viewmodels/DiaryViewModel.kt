package com.multimedia.writeyourthink.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo
import com.multimedia.writeyourthink.repositories.DiaryRepository
import kotlinx.coroutines.launch

class DiaryViewModel(
    val diaryRepository: DiaryRepository
) : ViewModel() {



    fun getData() : MutableLiveData<MutableList<Diary>> {
        val diaryData = MutableLiveData<MutableList<Diary>>()
        diaryRepository.getFirebaseData(diaryData)
        return diaryData
    }
    fun saveUser(userInfo: UserInfo) {
        diaryRepository.writeNewUserToFirebase(userInfo)
    }
    fun saveDiary(diary: Diary) = viewModelScope.launch {
            diaryRepository.writeNewDiaryToFirebase(diary)
        }
    fun deleteDiary(diary: Diary) = viewModelScope.launch {
        diaryRepository.deleteFromFirebase(diary)
    }
}