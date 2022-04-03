package com.example.mvvmruntracker.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Run (
    val img: Bitmap? = null,
    //When our run starts
    val timestamp: Long = 0L,
    val avgSpeedInKmh : Float = 0f,
    val distanceInMeters: Int = 0,
    //How long our run lasts
    val timeInMillis: Long = 0L,
    val caloriesBurned: Int = 0,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}