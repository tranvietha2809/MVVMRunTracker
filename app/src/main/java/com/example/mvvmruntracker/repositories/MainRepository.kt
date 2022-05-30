package com.example.mvvmruntracker.repositories

import com.example.mvvmruntracker.db.Run
import com.example.mvvmruntracker.db.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val runDAO: RunDAO
) {
    suspend fun insertRun(run:Run) = runDAO.insertRun(run)

    suspend fun deleteRun(run:Run) = runDAO.deleteRun(run)

    fun getAllRunSortedByDate() = runDAO.getAllRunSortedByDate()
    fun getAllRunSortedByAvgSpeed() = runDAO.getAllRunSortedByAvgSpeed()
    fun getAllRunSortedByDistance() = runDAO.getAllRunSortedByDistance()
    fun getAllRunSortedByDuration() = runDAO.getAllRunSortedByDuration()
    fun getAllRunSortedByCaloriesBurned() = runDAO.getAllRunSortedByCaloriesBurned()
    fun getTotalTimeInMillis() = runDAO.getTotalTimeInMillis()
    fun getTotalDistance() = runDAO.getTotalDistance()
    fun getTotalCaloriesBurned() = runDAO.getTotalCaloriesBurned()
    fun getTotalAvgSpeed() = runDAO.getTotalAvgSpeed()
}