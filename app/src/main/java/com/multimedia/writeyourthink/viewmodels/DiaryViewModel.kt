package com.multimedia.writeyourthink.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo
import com.multimedia.writeyourthink.repositories.DiaryRepository
import com.multimedia.writeyourthink.repositories.DiaryRepositoryImpl
import com.multimedia.writeyourthink.ui.fragments.DiaryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.ParseException

@HiltViewModel
class DiaryViewModel @Inject constructor(
    val diaryRepository: DiaryRepository
) : ViewModel() {
    private val sf = SimpleDateFormat("yyyy-MM-dd")
    private val dateFormatHms = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
    private val dateAndTimeFormat = SimpleDateFormat("yyyy-MM-dd(HH:mm:ss)", Locale.KOREA)
    private var _currentCalendarDate = MutableLiveData<Date>()

    val currentCalendarDate get() = _currentCalendarDate

    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState = _uiState.asStateFlow()

    init {

        fetchDiaryData {
            setDate(sf.format(Date()))
        }
    }

    fun fetchDiaryData(onNext: () -> Unit) {
        diaryRepository.getDiaryList { diaries, countMap, error ->
            diaries?.let { diaryList ->
                _uiState.update { it.copy(diaryList = diaryList, errorMassege = "") }
                onNext()
            }
            countMap?.let { count ->
                _uiState.update { it.copy(countMap = count, errorMassege = "") }
            }

            error?.let { err ->
                _uiState.update { it.copy(errorMassege = err.message.toString()) }
            }
        }
    }

    val calcCurrentTime = flow {
        emit(dateFormatHms.format(Date()))
        while (true) {
            delay(1000L)
            emit(dateFormatHms.format(Date()))
        }
    }

    fun setCalendarTitle(date: Date) {
        _currentCalendarDate.postValue(date)
    }

    fun setFilter(date: String) {
        val filtered = uiState.value.diaryList.filter {
            it.date.isNotEmpty() && it.date.substring(0, 10) == date
        }
        _uiState.update { it.copy(filteredByDate = filtered) }
    }

    fun setDate(date: String) = viewModelScope.launch {
        _uiState.update { it.copy(selectedDateTime = date) }
        setFilter(date)
    }

    fun dateUpDown(op: Int) {
        val currentDate = uiState.value.selectedDateTime
        val sdfForParse = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val day = currentDate.replace("-", "")
            .toInt()
            .plus(op)
            .toString()
            .runCatching {
                val parsed = sdfForParse.parse(this)
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsed)
            }.onFailure { it.printStackTrace() }
            .getOrNull() ?: return

        setDate(day)
    }

    fun saveUser(userInfo: UserInfo) {
        diaryRepository.setUserInfo(userInfo)
        _uiState.update { it.copy(userInfo = userInfo) }
    }

    fun saveDiary(diary: Diary) = viewModelScope.launch {
        diaryRepository.setDiary(diary)
    }

    fun deleteDiary(diary: Diary) = viewModelScope.launch {
        diaryRepository.deleteDiray(diary)
    }
}
