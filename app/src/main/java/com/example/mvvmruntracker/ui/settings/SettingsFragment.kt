package com.example.mvvmruntracker.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mvvmruntracker.databinding.SettingsFragmentBinding
import com.example.mvvmruntracker.other.SharedPrefs
import com.example.mvvmruntracker.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var settingsFragmentBinding: SettingsFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsFragmentBinding = SettingsFragmentBinding.inflate(layoutInflater)
        return settingsFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPrefs()
        settingsFragmentBinding.btnApplyChanges.setOnClickListener {
            val success = applyChangesToSharedPrefs()
            if (success) {
                Snackbar.make(view, "Saved changes", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(view, "Please fill out all the fields", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun loadFieldsFromSharedPrefs() {
        val name = sharedPrefs.getString(SharedPrefs.KEY_NAME, "")
        val weight = sharedPrefs.getFloat(SharedPrefs.KEY_WEIGHT, 80f)
        settingsFragmentBinding.etName.setText(name)
        settingsFragmentBinding.etWeight.setText(weight.toString())
    }

    private fun applyChangesToSharedPrefs(): Boolean {
        val nameText = settingsFragmentBinding.etName.text.toString()
        val weightText = settingsFragmentBinding.etWeight.text.toString()
        if (nameText.isEmpty() || weightText.isEmpty()) {
            return false
        }
        sharedPrefs.edit()
            .putString(SharedPrefs.KEY_NAME, nameText)
            .putFloat(SharedPrefs.KEY_WEIGHT, weightText.toFloat())
            .apply()
        val toolbarText = "Let's go $nameText"
        (requireActivity() as MainActivity).activityMainBinding.tvToolbarTitle.text = toolbarText
        return true
    }
}