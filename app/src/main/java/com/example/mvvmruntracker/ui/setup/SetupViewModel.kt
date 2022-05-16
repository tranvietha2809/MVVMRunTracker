package com.example.mvvmruntracker.ui.setup

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.mvvmruntracker.other.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(private val sharedPref: SharedPreferences): ViewModel() {
    fun saveNameToSharedPrefs(name: String) {
        sharedPref.edit().putString(SharedPrefs.KEY_NAME, name).apply()
    }

    fun saveWeightToSharedPrefs(weight: Float) {
        sharedPref.edit().putFloat(SharedPrefs.KEY_WEIGHT, weight).apply()
    }

    fun saveFirstTimeToggleToSharedPrefs(isFirstTime: Boolean) {
        sharedPref.edit().putBoolean(SharedPrefs.KEY_FIRST_TIME_TOGGLE, isFirstTime).apply()
    }
}