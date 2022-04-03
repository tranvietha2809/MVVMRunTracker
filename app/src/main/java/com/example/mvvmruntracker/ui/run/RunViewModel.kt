package com.example.mvvmruntracker.ui.run

import androidx.lifecycle.ViewModel
import com.example.mvvmruntracker.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class RunViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {
}