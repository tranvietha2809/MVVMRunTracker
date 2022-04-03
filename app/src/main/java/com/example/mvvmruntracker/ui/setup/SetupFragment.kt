package com.example.mvvmruntracker.ui.setup

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mvvmruntracker.R
import com.example.mvvmruntracker.databinding.SetupFragmentBinding
import kotlinx.android.synthetic.main.setup_fragment.*

class SetupFragment : Fragment(R.layout.setup_fragment) {

    companion object {
        fun newInstance() = SetupFragment()
    }

    private val viewModel: SetupViewModel by viewModels()
    private lateinit var setupFragmentBinding: SetupFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupFragmentBinding = SetupFragmentBinding.inflate(layoutInflater)
        return setupFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragmentBinding.tvContinue.setOnClickListener {
            findNavController().navigate(R.id.action_setupFragment_to_runFragment)
        }
    }
}