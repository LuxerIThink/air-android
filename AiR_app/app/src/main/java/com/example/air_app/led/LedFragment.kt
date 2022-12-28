package com.example.air_app.led


import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.air_app.R
import com.example.air_app.charts.MeasurementViewModel
import com.example.air_app.data.User
import com.example.air_app.databinding.LedFragmentBinding
import com.example.air_app.databinding.MeasurementsFragmentBinding


class LedFragment : Fragment() {
    private lateinit var ledViewModel: LedViewModel
    private lateinit var binding: LedFragmentBinding

    private var disp: LedDisplayModel? = null
    private var preview: LedModel? = null
    private val nullColor = 0x00000000

    private var colorView
            : View? = null
    private var urlText
            : EditText? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.led_fragment,container,false)
        ledViewModel = ViewModelProvider(this).get(LedViewModel::class.java)
        binding.ledViewModel = ledViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.sendBtn.setOnClickListener { sendControlRequest() }
        binding.clearBtn.setOnClickListener { clearDisplay() }

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val ip = sharedPref?.getString("IP", "localhost")
        val port = sharedPref?.getInt("PORT", 80)
        val currentUser = User(ip=ip!!, port = port!!)
        binding.ledViewModel!!.loadUser(currentUser)

        binding.ledViewModel!!.makeAlert.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
                builder.setMessage(it)
                builder.setPositiveButton(R.string.label_ok) { _, _ -> }
                builder.create().show()
            }
        }

        disp = LedDisplayModel()
        preview = LedModel()

        val ledTable = binding.ledTable
        for (y in 0 until disp!!.sizeY) {
            //
            val ledRow = TableRow(requireContext())
            ledRow.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
            )
            for (x in 0 until disp!!.sizeX) ledRow.addView(addLedIndicatorToTableLayout(x, y))
            ledTable.addView(ledRow)
        }

        colorView =binding.colorView
        urlText = binding.urlText


        val redSeekBar = binding.seekBarR
        redSeekBar.max = 255
        redSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                preview!!.R = progressChangedValue
                setLedViewColor(colorView, preview!!.color)
            }
        })
        val greenSeekBar = binding.seekBarG
        greenSeekBar.max = 255
        greenSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                preview!!.G = progressChangedValue
                setLedViewColor(colorView, preview!!.color)
            }
        })
        val blueSeekBar = binding.seekBarB
        blueSeekBar.max = 255
        blueSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                preview!!.B = progressChangedValue
                setLedViewColor(colorView, preview!!.color)
            }
        })

        clearDisplay()

        return binding.root
    }


    private fun ledTagToIndex(tag: String): IntArray {
        // Tag: 'LEDxy"
        return intArrayOf(
            Character.getNumericValue(tag[3]),
            Character.getNumericValue(tag[4])
        )
    }


    private fun ledIndexToTag(x: Int, y: Int): String? {
        return "LED" + Integer.toString(x) + Integer.toString(y)
    }


    private fun addLedIndicatorToTableLayout(x: Int, y: Int): RelativeLayout? {

        val border = RelativeLayout(requireContext())

        val ledIndWidth = resources.getDimension(R.dimen.ledIndWidth).toInt()
        val ledIndHeight = resources.getDimension(R.dimen.ledIndHeight).toInt()
        val border_params = TableRow.LayoutParams(ledIndWidth, ledIndHeight)
        val borderThickness_px = resources.getDimension(R.dimen.borderThickness).toInt()
        border_params.setMargins(
            borderThickness_px, borderThickness_px,
            borderThickness_px, borderThickness_px
        )
        border_params.gravity = Gravity.CENTER
        border_params.weight = 1f
        border.layoutParams = border_params

        border.background = resources.getDrawable(R.drawable.led_border)

        val led = View(requireContext())

        val led_params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        val ledIndMarginTopBottom_px =
            resources.getDimension(R.dimen.ledIndMarginTopBottom).toInt()
        val ledIndMarginLeftRight_px =
            resources.getDimension(R.dimen.ledIndMarginLeftRight).toInt()
        led_params.setMargins(
            ledIndMarginLeftRight_px, ledIndMarginTopBottom_px,
            ledIndMarginLeftRight_px, ledIndMarginTopBottom_px
        )
        led.layoutParams = led_params

        led.background = resources.getDrawable(R.drawable.led_view)


        led.setOnClickListener(led_onClick)

        led.tag = ledIndexToTag(x, y)


        border.addView(led)
        return border
    }


    private fun setLedViewColor(v: View?, color: Int) {
        val backgroundColor = v!!.background
        if (backgroundColor is ShapeDrawable) {
            backgroundColor.paint.color = color
        } else if (backgroundColor is GradientDrawable) {
            backgroundColor.setColor(color)
        } else if (backgroundColor is ColorDrawable) {
            backgroundColor.color = color
        }
    }


    private val led_onClick = View.OnClickListener { v ->

        preview?.let { setLedViewColor(v, it.color) }
        val pos = ledTagToIndex(v.tag as String)
        disp?.setLedModel(pos[0], pos[1], preview)
    }


    fun clearDisplay() {

        val tb = binding.ledTable
        var ledInd: View?
        for (i in 0 until (disp?.sizeX ?: 0)) {
            for (j in 0 until (disp?.sizeY ?: 0)) {
                ledInd = tb.findViewWithTag(ledIndexToTag(i, j))
                setLedViewColor(ledInd, nullColor)
            }
        }


        disp?.clearModel()


        binding.ledViewModel!!.client.method = "PUT"
        binding.ledViewModel!!.requestHelper({}, disp?.controlJsonArray.toString())
        //server.putControlRequest(disp?.controlJsonArray)
    }


    fun sendControlRequest() {
        binding.ledViewModel!!.client.method = "PUT"
        binding.ledViewModel!!.requestHelper({}, disp?.controlJsonArray.toString())
    }


}