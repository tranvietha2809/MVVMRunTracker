package com.example.mvvmruntracker

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mvvmruntracker.other.SharedPrefs
import com.example.mvvmruntracker.ui.setup.SetupViewModel
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetupViewModelTest {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var setupViewModel : SetupViewModel
    private lateinit var context: Context

    val name = "NAME"
    val weight = 4f
    val isFirstTimeToggle = false

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        sharedPreferences = context.getSharedPreferences(SharedPrefs.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        setupViewModel = SetupViewModel(sharedPreferences)
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun saveUserNameToSharedPrefs() {
        setupViewModel.saveNameToSharedPrefs(name)
        assertEquals(sharedPreferences.getString(SharedPrefs.KEY_NAME, ""), name)
    }

    @Test
    fun saveWeightToSharedPrefs() {
        setupViewModel.saveWeightToSharedPrefs(weight)
        assertEquals(sharedPreferences.getFloat(SharedPrefs.KEY_WEIGHT, 0f), weight)
    }

    @Test
    fun saveFirstTimeToSharedPrefs() {
        setupViewModel.saveWeightToSharedPrefs(weight)
        assertEquals(sharedPreferences.getFloat(SharedPrefs.KEY_WEIGHT, 0f), weight)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().apply()
    }
}