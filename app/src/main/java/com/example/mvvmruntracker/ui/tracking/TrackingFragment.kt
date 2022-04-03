package com.example.mvvmruntracker.ui.tracking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mvvmruntracker.databinding.TrackingFragmentBinding
import com.example.mvvmruntracker.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.mvvmruntracker.services.TrackingService
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.tracking_fragment.*

class TrackingFragment : Fragment() {

    companion object {
        fun newInstance() = TrackingFragment()
    }

    private val viewModel: TrackingViewModel by viewModels()
    private lateinit var trackingFragmentBinding: TrackingFragmentBinding

    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        trackingFragmentBinding = TrackingFragmentBinding.inflate(layoutInflater)
        trackingFragmentBinding.mapView.onCreate(savedInstanceState)
        trackingFragmentBinding.mapView.getMapAsync {
            map = it
        }
        return trackingFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        trackingFragmentBinding.btnToggleRun.setOnClickListener {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
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