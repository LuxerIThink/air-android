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
import androidx.lifecycle.viewModelScope
import com.example.air_app.MyTimer
import com.example.air_app.R
import com.example.air_app.data.User
import com.example.air_app.databinding.MeasurementsFragmentBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class MeasurementFragment : Fragment() {
    private lateinit var measurementViewModel: MeasurementViewModel
    private lateinit var binding: MeasurementsFragmentBinding
    private lateinit var chart: GraphView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.measurements_fragment,container,false)
        measurementViewModel = ViewModelProvider(this).get(MeasurementViewModel::class.java)
        binding.measurementViewModel = measurementViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val ip = sharedPref?.getString("IP", "localhost")
        val port = sharedPref?.getInt("PORT", 80)
        val currentUser = User(ip=ip!!, port = port!!)
        binding.measurementViewModel!!.loadUser(currentUser)

        binding.measurementViewModel!!.makeAlert.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
                builder.setMessage(it)
                builder.setPositiveButton(R.string.label_ok) { _, _ -> }
                builder.create().show()
            }
        }

        ChartInit()

        binding.measurementViewModel!!.running.observe(viewLifecycleOwner){
            if(it){
                binding.measurementViewModel!!.timer.startTimer()
            }else{
                binding.measurementViewModel!!.timer.stopTimer()
            }
        }
        binding.measurementViewModel!!.unit.observe(viewLifecycleOwner){
            chart.gridLabelRenderer.verticalAxisTitle = "Amplitude [${it}]"
            chart.legendRenderer.resetStyles()
            chart.legendRenderer.isVisible = true
            chart.legendRenderer.align = LegendRenderer.LegendAlign.TOP
            chart.legendRenderer.textSize = 30f
        }
        binding.measurementViewModel!!.minX.observe(viewLifecycleOwner){
            binding.chart.viewport.setMinX(it)
        }
        binding.measurementViewModel!!.maxX.observe(viewLifecycleOwner){
            binding.chart.viewport.setMaxX(it)
        }

        binding.measurementViewModel!!.sampleMax = (chart.viewport.getMaxX(false) / binding.measurementViewModel!!.sampleTime).toInt()
        return binding.root
    }

    private fun ChartInit() {
        // https://github.com/jjoe64/GraphView/wiki
        chart = binding.chart
        chart.addSeries(binding.measurementViewModel!!.signal1)
        //chart.addSeries(binding.measurementViewModel!!.signal2)
        chart.viewport.isXAxisBoundsManual = true
        chart.viewport.setMinX(0.0)
        chart.viewport.setMaxX(100.0)
        //chart.viewport.setMaxX(10.0)
        chart.viewport.isScrollable = true
        chart.viewport.isYAxisBoundsManual = false
        binding.measurementViewModel!!.signal1.color = Color.RED
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

    private fun Space(n: Int): String? {
        return String(CharArray(n)).replace('\u0000', ' ')
    }

    override fun onStart() {
        super.onStart()
        if(binding.measurementViewModel!!.running.value!!) {
            binding.measurementViewModel!!.timer.startTimer()
        }
    }

    override fun onStop() {
        super.onStop()
        binding.measurementViewModel!!.timer.stopTimer()
    }

}
