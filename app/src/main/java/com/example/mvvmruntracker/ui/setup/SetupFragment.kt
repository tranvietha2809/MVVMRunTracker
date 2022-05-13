package com.example.mvvmruntracker.ui.setup

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.mvvmruntracker.R
import com.example.mvvmruntracker.databinding.SetupFragmentBinding
import com.example.mvvmruntracker.other.SharedPrefs
import com.example.mvvmruntracker.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.setup_fragment) {

    companion object {
        fun newInstance() = SetupFragment()
    }

    private val viewModel: SetupViewModel by viewModels()
    private lateinit var setupFragmentBinding: SetupFragmentBinding

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupFragmentBinding = SetupFragmentBinding.inflate(layoutInflater)
        return setupFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!sharedPref.getBoolean(SharedPrefs.KEY_FIRST_TIME_TOGGLE, true)){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(R.id.action_setupFragment_to_runFragment, savedInstanceState, navOptions)
        }
        setupFragmentBinding.tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPrefs()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun writePersonalDataToSharedPrefs(): Boolean {
        val name = setupFragmentBinding.etName.text.toString()
        val weight = setupFragmentBinding.etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(SharedPrefs.KEY_NAME, name)
            .putFloat(SharedPrefs.KEY_WEIGHT, weight.toFloat())
            .putBoolean(SharedPrefs.KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        val toolbarText = "Let's go, $name!"
        (requireActivity() as MainActivity).activityMainBinding.tvToolbarTitle.text = toolbarText
        return true
    }
}