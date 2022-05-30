package com.example.mvvmruntracker.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mvvmruntracker.R
import com.example.mvvmruntracker.databinding.StatisticsFragmentBinding
import com.example.mvvmruntracker.other.CustomMarkerView
import com.example.mvvmruntracker.other.TrackingUtility
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.statistics_fragment.*
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    companion object {
        fun newInstance() = StatisticsFragment()
    }

    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var statisticsFragmentBinding: StatisticsFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        statisticsFragmentBinding = StatisticsFragmentBinding.inflate(layoutInflater)
        return statisticsFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setupBarChart()
    }

    private fun subscribeToObservers() {
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                statisticsFragmentBinding.tvTotalTime.text = totalTimeRun
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalDistance = round((it / 1000f) * 10f) / 10f
                val totalDistanceText = "${totalDistance}km"
                statisticsFragmentBinding.tvTotalDistance.text = totalDistanceText
            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed = round(it * 10f) / 10f
                val avgSpeedString = "${avgSpeed}km/h"
                statisticsFragmentBinding.tvTotalDistance.text = avgSpeedString
            }
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val calories = "${it}kcal"
                statisticsFragmentBinding.tvTotalCalories.text = calories
            }
        })
        viewModel.runSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAvgSpeeds =
                    it.indices.map { index -> BarEntry(index.toFloat(), it[index].avgSpeedInKmh) }
                val barDataSet = BarDataSet(allAvgSpeeds, "Avg Speed Over Time").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }
                barChart.data = BarData(barDataSet)
                barChart.marker =
                    CustomMarkerView(it.reversed(), requireContext(), R.layout.marker_view)
                barChart.invalidate()
            }
        })
    }

    private fun setupBarChart() {
        statisticsFragmentBinding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        statisticsFragmentBinding.barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        statisticsFragmentBinding.barChart.apply {
            description.text = "Avg Speed over time"
            legend.isEnabled = false
        }
    }
}