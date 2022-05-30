package com.example.mvvmruntracker.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvvmruntracker.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(mainRepository: MainRepository) : ViewModel() {
    val totalTimeRun =
        mainRepository.getTotalTimeInMillis().asLiveData(viewModelScope.coroutineContext)
    val totalDistance =
        mainRepository.getTotalDistance().asLiveData(viewModelScope.coroutineContext)
    val totalCaloriesBurned =
        mainRepository.getTotalCaloriesBurned().asLiveData(viewModelScope.coroutineContext)
    val totalAvgSpeed =
        mainRepository.getTotalAvgSpeed().asLiveData(viewModelScope.coroutineContext)
    val runSortedByDate =
        mainRepository.getAllRunSortedByDate().asLiveData(viewModelScope.coroutineContext)
}