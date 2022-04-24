package com.example.mvvmruntracker.ui.tracking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mvvmruntracker.databinding.TrackingFragmentBinding
import com.example.mvvmruntracker.other.Constants.ACTION_PAUSE_SERVICE
import com.example.mvvmruntracker.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.mvvmruntracker.other.Constants.MAP_ZOOM
import com.example.mvvmruntracker.other.Constants.POLYLINE_COLOR
import com.example.mvvmruntracker.other.Constants.POLYLINE_WIDTH
import com.example.mvvmruntracker.services.Polyline
import com.example.mvvmruntracker.services.TrackingService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.tracking_fragment.*

class TrackingFragment : Fragment() {

    companion object {
        fun newInstance() = TrackingFragment()
    }

    private val viewModel: TrackingViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private lateinit var trackingFragmentBinding: TrackingFragmentBinding

    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        trackingFragmentBinding = TrackingFragmentBinding.inflate(layoutInflater)
        trackingFragmentBinding.mapView.onCreate(savedInstanceState)

        return trackingFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        trackingFragmentBinding.btnToggleRun.setOnClickListener {
            toggleRun()
        }
        trackingFragmentBinding.mapView.getMapAsync {
            map = it
            addAllPolyline()
        }
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
    }

    private fun toggleRun() {
        if(isTracking) {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if(!isTracking) {
            trackingFragmentBinding.btnToggleRun.text = "Start"
            trackingFragmentBinding.btnFinishRun.visibility = View.VISIBLE
        } else {
            trackingFragmentBinding.btnToggleRun.text = "Stop"
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