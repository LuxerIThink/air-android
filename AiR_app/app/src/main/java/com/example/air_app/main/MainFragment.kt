package com.example.air_app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.air_app.R
import com.example.air_app.databinding.MainFragmentBinding


class MainFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.main_fragment,container,false)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.mainViewModel!!.eventNavigateToChart.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToMeasurementFragment())
            }
        }
        binding.mainViewModel!!.eventNavigateToLed.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToLedFragment())
            }
        }
        binding.mainViewModel!!.eventNavigateToTable.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToTableFragment())
            }
        }

        return binding.root
    }
}