package com.example.mvvmruntracker.ui.run

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mvvmruntracker.R
import com.example.mvvmruntracker.databinding.RunFragmentBinding
import com.example.mvvmruntracker.other.TrackingUtility
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import timber.log.Timber

@AndroidEntryPoint
class RunFragment : Fragment() {

    companion object {
        fun newInstance() = RunFragment()
    }

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val BACKGROUND_LOCATION_PERMISSION =
        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    private val PERMISSION_ID = 42

    private val viewModel: RunViewModel by viewModels()
    private lateinit var runFragmentBinding: RunFragmentBinding
    private val DENIED: String = "DENIED"
    private val EXPLAINED: String = "EXPLAINED"
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val deniedList: List<String> = result.filter {
            !it.value
        }.map {
            it.key
        }
        when {
            deniedList.isNotEmpty() -> {
                val map = deniedList.groupBy { permissions ->
                    if (shouldShowRequestPermissionRationale(permissions)) DENIED else EXPLAINED
                }
                map[DENIED]?.let {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        it.toTypedArray(), PERMISSION_ID
                    )
                }
                map[EXPLAINED]?.let {
                    AppSettingsDialog.Builder(this).build().show()
                }
            }
            else -> {
                Timber.d("All permissions granted")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        runFragmentBinding = RunFragmentBinding.inflate(layoutInflater)
        return runFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationPermissionRequest.launch(REQUIRED_PERMISSIONS)
        runFragmentBinding.fab.setOnClickListener {
            if (!TrackingUtility.hasBackgroundLocationPermission(requireContext())) {
                val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                    .setTitle("Need Background Location Permission")
                    .setMessage("To Start Run, MVVM App Tracking Runner requires background location permission")
                    .setPositiveButton("Go To Settings Page") { _, _ ->
                        AppSettingsDialog.Builder(this).build().show()
                    }
                    .setNegativeButton("No") { dialogInterface, _ -> dialogInterface.cancel() }
                    .create()
                dialog.show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }
}