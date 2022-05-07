package com.example.mvvmruntracker.ui.tracking

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mvvmruntracker.R
import com.example.mvvmruntracker.databinding.TrackingFragmentBinding
import com.example.mvvmruntracker.db.Run
import com.example.mvvmruntracker.other.Constants.ACTION_PAUSE_SERVICE
import com.example.mvvmruntracker.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.mvvmruntracker.other.Constants.ACTION_STOP_SERVICE
import com.example.mvvmruntracker.other.Constants.MAP_ZOOM
import com.example.mvvmruntracker.other.Constants.POLYLINE_COLOR
import com.example.mvvmruntracker.other.Constants.POLYLINE_WIDTH
import com.example.mvvmruntracker.other.TrackingUtility
import com.example.mvvmruntracker.services.Polyline
import com.example.mvvmruntracker.services.TrackingService
import com.example.mvvmruntracker.ui.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.tracking_fragment.*
import java.util.*
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    companion object {
        fun newInstance() = TrackingFragment()
    }

    private val viewModel: TrackingViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private lateinit var trackingFragmentBinding: TrackingFragmentBinding

    private var map: GoogleMap? = null

    private var curTimeInMillis = 0L

    private var menu: Menu? = null

    private var weight = 80f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        trackingFragmentBinding = TrackingFragmentBinding.inflate(layoutInflater)
        trackingFragmentBinding.mapView.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        return trackingFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        trackingFragmentBinding.btnToggleRun.setOnClickListener {
            toggleRun()
        }
        trackingFragmentBinding.btnFinishRun.setOnClickListener {
            zoomToSeeWholeRunningTrack()
            endRunAndSaveToDb()
        }
        trackingFragmentBinding.mapView.getMapAsync {
            map = it
            addAllPolyline()
        }
        subscribeToObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (curTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the current run and delete all its data?")
            .setIcon(R.drawable.ic_delete_black_24dp)
            .setPositiveButton("Yes") { _, _ ->
                stopRun()
            }
            .setNegativeButton("No") { dialogInterface, _ -> dialogInterface.cancel() }
            .create()
        dialog.show()
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner) {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        }

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner) {
            curTimeInMillis = it
            val formattedTime =
                TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, includeMillis = true)
            trackingFragmentBinding.tvTimer.text = formattedTime
        }
    }

    private fun toggleRun() {
        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking) {
            trackingFragmentBinding.btnToggleRun.text = "Start"
            trackingFragmentBinding.btnFinishRun.visibility = View.VISIBLE
        } else {
            trackingFragmentBinding.btnToggleRun.text = "Stop"
            menu?.getItem(0)?.isVisible = true
            trackingFragmentBinding.btnFinishRun.visibility = View.GONE
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), MAP_ZOOM)
            )
        }
    }

    private fun zoomToSeeWholeRunningTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (position in polyline) {
                bounds.include(position)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                trackingFragmentBinding.mapView.width,
                trackingFragmentBinding.mapView.height,
                (trackingFragmentBinding.mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.polylineToLength(polyline).toInt()
            }
            val avgSpeed =
                round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60f / 60f) * 10) / 10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run =
                Run(bmp, dateTimeStamp, avgSpeed, distanceInMeters, curTimeInMillis, caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(
                (requireActivity() as MainActivity).activityMainBinding.root,
                "Run saved successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    private fun addAllPolyline() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions =
                PolylineOptions()
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH)
                    .add(preLastLng)
                    .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        trackingFragmentBinding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        trackingFragmentBinding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        trackingFragmentBinding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        trackingFragmentBinding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        trackingFragmentBinding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        trackingFragmentBinding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        trackingFragmentBinding.mapView.onSaveInstanceState(outState)
    }
}