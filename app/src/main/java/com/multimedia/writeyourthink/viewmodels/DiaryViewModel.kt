package com.multimedia.writeyourthink.viewmodels

import android.util.Log
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
    var diaryData = MutableLiveData<MutableList<Diary>>()
    var filteredList = MutableLiveData<MutableList<Diary>>()
    var selectedDateTime = MutableLiveData<String>()

    fun setFilter() {
        filteredList.value = diaryData.value?.filter {
            it.date.isNotEmpty() && it.date.substring(0, 10) == selectedDateTime.value
        }?.toMutableList()

    }
    fun setDate(date: String) {
        selectedDateTime.value = date
    }
    fun getData(): MutableLiveData<MutableList<Diary>> {
        diaryRepository.getFirebaseData(diaryData, selectedDateTime)
        //diaryData.value!!.filter { it.date.isNotEmpty() }?.toMutableList()
        Log.d("Lee", diaryData.value?.isEmpty().toString())
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