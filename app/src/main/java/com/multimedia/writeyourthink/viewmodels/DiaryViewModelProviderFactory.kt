package com.multimedia.writeyourthink.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.multimedia.writeyourthink.repositories.DiaryRepository

class DiaryViewModelProviderFactory(
    val diaryRepository: DiaryRepository
) : ViewModelProvider.Factory {
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        return DiaryViewModel(diaryRepository) as T
    }
}