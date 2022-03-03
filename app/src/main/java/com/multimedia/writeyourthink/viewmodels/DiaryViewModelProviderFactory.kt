package com.multimedia.writeyourthink.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.multimedia.writeyourthink.repositories.FirebaseRepository

class DiaryViewModelProviderFactory(
    val firebaseRepository: FirebaseRepository
) : ViewModelProvider.Factory {
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        return DiaryViewModel(firebaseRepository) as T
    }
}