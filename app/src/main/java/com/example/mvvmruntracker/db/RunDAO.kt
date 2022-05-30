package com.example.mvvmruntracker.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getAllRunSortedByDate() : Flow<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY avgSpeedInKmh DESC")
    fun getAllRunSortedByAvgSpeed() : Flow<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY distanceInMeters DESC")
    fun getAllRunSortedByDistance() : Flow<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY timeInMillis DESC")
    fun getAllRunSortedByDuration() : Flow<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunSortedByCaloriesBurned() : Flow<List<Run>>

    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis() : Flow<Long>

    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalDistance() : Flow<Int>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned() : Flow<Int>

    @Query("SELECT AVG(avgSpeedInKmh) FROM running_table")
    fun getTotalAvgSpeed() : Flow<Float>
}