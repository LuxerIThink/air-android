package com.example.air_app.charts

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.air_app.R
import com.example.air_app.data.User
import com.example.air_app.databinding.ChartFragmentBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer

class ChartFragment : Fragment() {
    private lateinit var chartViewModel: ChartViewModel
    private lateinit var binding: ChartFragmentBinding
    private lateinit var chart: GraphView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.chart_fragment,container,false)
        chartViewModel = ViewModelProvider(this)[ChartViewModel::class.java]
        binding.chartViewModel = chartViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val ip = sharedPref?.getString("IP", "localhost")
        val port = sharedPref?.getInt("PORT", 80)
        val currentUser = User(ip=ip!!, port = port!!)
        binding.chartViewModel!!.loadUser(currentUser)

        binding.chartViewModel!!.makeAlert.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
                builder.setMessage(it)
                builder.setPositiveButton(R.string.label_ok) { _, _ -> }
                builder.create().show()
            }
        }

        chartInit()

        binding.chartViewModel!!.running.observe(viewLifecycleOwner){
            if(it){
                if(!binding.chartViewModel!!.timer.running){
                    binding.chartViewModel!!.reset()
                }
                binding.chartViewModel!!.timer.startTimer()
            }else{
                binding.chartViewModel!!.timer.stopTimer()
            }
        }
        binding.chartViewModel!!.unit.observe(viewLifecycleOwner){
            chart.gridLabelRenderer.verticalAxisTitle = "Amplitude [${it}]"
            chart.legendRenderer.resetStyles()
            chart.legendRenderer.isVisible = true
            chart.legendRenderer.align = LegendRenderer.LegendAlign.TOP
            chart.legendRenderer.textSize = 30f
        }
        binding.chartViewModel!!.minX.observe(viewLifecycleOwner){
            binding.chart.viewport.setMinX(it)
        }
        binding.chartViewModel!!.maxX.observe(viewLifecycleOwner){
            binding.chart.viewport.setMaxX(it)
        }

        binding.chartViewModel!!.sampleMax = (chart.viewport.getMaxX(false) / binding.chartViewModel!!.sampleTime).toInt()
        return binding.root
    }

    private fun chartInit() {
        // https://github.com/jjoe64/GraphView/wiki
        chart = binding.chart
        chart.addSeries(binding.chartViewModel!!.signal1)
        //chart.addSeries(binding.measurementViewModel!!.signal2)
        chart.viewport.isXAxisBoundsManual = true
        chart.viewport.setMinX(0.0)
        chart.viewport.setMaxX(100.0)
        //chart.viewport.setMaxX(10.0)
        chart.viewport.isScrollable = true
        chart.viewport.isYAxisBoundsManual = false
        binding.chartViewModel!!.signal1.color = Color.BLUE
        chart.legendRenderer.isVisible = true
        chart.legendRenderer.align = LegendRenderer.LegendAlign.TOP
        chart.legendRenderer.textSize = 30f
        chart.gridLabelRenderer.textSize = 20f
        chart.gridLabelRenderer.verticalAxisTitle = "Amplitude [-]"
        chart.gridLabelRenderer.horizontalAxisTitle = "Time [s]"
        chart.gridLabelRenderer.numHorizontalLabels = 9
        chart.gridLabelRenderer.numVerticalLabels = 7
        chart.gridLabelRenderer.padding = 35
    }

    override fun onStart() {
        super.onStart()
        if(binding.chartViewModel!!.running.value!!) {
            binding.chartViewModel!!.timer.startTimer()
        }
    }

    override fun onStop() {
        super.onStop()
        binding.chartViewModel!!.timer.stopTimer()
    }

}
