package com.multimedia.writeyourthink.ui.fragments

import androidx.annotation.Keep
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.models.UserInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Keep
data class DiaryUiState(
    val diaryList: List<Diary> = listOf(),
    val filteredByDate: List<Diary> = listOf(),
    val countMap: Map<String, Int> = mapOf(),
    val selectedDateTime: String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        .format(Date()),
    val userInfo: UserInfo = UserInfo(),
    val calendarDate: String = "",
    val errorMassege: String = ""
)