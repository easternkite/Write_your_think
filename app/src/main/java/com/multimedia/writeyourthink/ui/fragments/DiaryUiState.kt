package com.multimedia.writeyourthink.ui.fragments

import androidx.annotation.Keep
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo

@Keep
data class DiaryUiState(
    val diaryList: List<Diary> = listOf(),
    val filteredByDate: List<Diary> = listOf(),
    val countMap: Map<String, Int> = mapOf(),
    val selectedDateTime: String = "",
    val userInfo: UserInfo = UserInfo(),
    val calendarDate: String = "",
    val errorMassege: String = ""
)