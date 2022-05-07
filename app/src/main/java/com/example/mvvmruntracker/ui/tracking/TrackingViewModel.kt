package com.example.mvvmruntracker.ui.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmruntracker.db.Run
import com.example.mvvmruntracker.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel(){
    fun insertRun(run: Run) {
        viewModelScope.launch {
            mainRepository.insertRun(run)
        }
    }
}