package com.example.mvvmruntracker.ui.run

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvmruntracker.db.Run
import com.example.mvvmruntracker.other.SortType
import com.example.mvvmruntracker.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class RunViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {
    private val runSortedByDate = mainRepository.getAllRunSortedByDate()
    private val runSortedByDistance = mainRepository.getAllRunSortedByDistance()
    private val runSortedByAvgSpeed = mainRepository.getAllRunSortedByAvgSpeed()
    private val runSortedByCaloriesBurned = mainRepository.getAllRunSortedByCaloriesBurned()
    private val runSortedByDuration = mainRepository.getAllRunSortedByDuration()

    val runs = MediatorLiveData<List<Run>>()
    var sortType = SortType.DATE

    init {
        runs.addSource(runSortedByDate){ res ->
            if(sortType == SortType.DATE){
                runs.value = res
            }
        }

        runs.addSource(runSortedByDistance){ res ->
            if(sortType == SortType.DISTANCE){
                runs.value = res
            }
        }

        runs.addSource(runSortedByAvgSpeed){ res ->
            if(sortType == SortType.AVG_SPEED){
                runs.value = res
            }
        }

        runs.addSource(runSortedByCaloriesBurned){ res ->
            if(sortType == SortType.CALORIES_BURNED){
                runs.value = res
            }
        }

        runs.addSource(runSortedByDuration){ res ->
            if(sortType == SortType.RUNNING_TIME){
                runs.value = res
            }
        }
    }

    fun sortRuns(sortType: SortType) = when (sortType){
        SortType.DATE -> runSortedByDate.value?.let { runs.value = it }
        SortType.DISTANCE -> runSortedByDistance.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runSortedByAvgSpeed.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runSortedByCaloriesBurned.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runSortedByDuration.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }
}