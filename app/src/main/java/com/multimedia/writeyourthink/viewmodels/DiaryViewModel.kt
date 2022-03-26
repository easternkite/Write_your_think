package com.multimedia.writeyourthink.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo
import com.multimedia.writeyourthink.repositories.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class DiaryViewModel @Inject constructor(
    val diaryRepository: DiaryRepository
) : ViewModel() {
    private val dateFormatHms = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
    private var _diaryData = MutableLiveData<MutableList<Diary>>()
    private var _filteredList = MutableLiveData<MutableList<Diary>>()
    private var _selectedDateTime = MutableLiveData<String>()
    private var _userInfo = MutableLiveData<UserInfo>()
    private var _countDiaryContents = MutableLiveData<HashMap<String, Int>>()
    private var _currentCalendarDate = MutableLiveData<Date>()

    val diaryData get() = getData()
    val filteredList get() = _filteredList
    val selectedDateTime get() = _selectedDateTime
    val userInfo get() = _userInfo
    val countDiaryContents get() = _countDiaryContents
    val currentCalendarDate get() = _currentCalendarDate

    val calcCurrentTime = flow {
        emit(dateFormatHms.format(Date()))
        while(true) {
            delay(1000L)
            emit(dateFormatHms.format(Date()))
        }
    }

    private var _currentTime = MutableLiveData<String>()
    val currentTime get() = _currentTime

    fun setFilter() {
        _filteredList.value = _diaryData.value?.filter {
            it.date.isNotEmpty() && it.date.substring(0, 10) == _selectedDateTime.value
        }?.toMutableList()

    }
    fun setCalendarTitle(date: Date) {
        _currentCalendarDate.postValue(date)
    }

    fun setDate(date: String) {
        _selectedDateTime.value = date
    }
    private fun getData(): MutableLiveData<MutableList<Diary>> {
        diaryRepository.getFirebaseData(_diaryData, _countDiaryContents)
        return _diaryData
    }

    fun saveUser(userInfo: UserInfo) {
        diaryRepository.writeNewUserToFirebase(userInfo)
        this._userInfo.postValue(userInfo)
    }

    fun saveDiary(diary: Diary) = viewModelScope.launch {
        diaryRepository.writeNewDiaryToFirebase(diary)
    }

    fun deleteDiary(diary: Diary) = viewModelScope.launch {
        diaryRepository.deleteFromFirebase(diary)
    }
}
