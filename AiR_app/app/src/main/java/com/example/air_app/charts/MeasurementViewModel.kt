package com.example.air_app.charts

import android.annotation.SuppressLint
import android.widget.RadioGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.air_app.Event
import com.example.air_app.MyTimer
import com.example.air_app.R
import com.example.air_app.data.User
import com.example.air_app.server.Client
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.ceil


@SuppressLint("StaticFieldLeak")
class MeasurementViewModel: ViewModel() {
    var requestIndicator = false
    var signal1 =  LineGraphSeries<DataPoint>()
    var signal2 =  LineGraphSeries<DataPoint>()
    private var k = 0
    val sampleTime = 0.1
    var sampleMax = 100000
    var timer = MyTimer((1000*sampleTime).toLong(),::getServerData0)
    var currentName: String? = null
    private val _makeAlert = MutableLiveData<Event<String>>()
    val makeAlert : LiveData<Event<String>>
        get() = _makeAlert

    var _minX = MutableLiveData<Double>()
    val minX : LiveData<Double>
        get() = _minX

    var _maxX = MutableLiveData<Double>()
    val maxX : LiveData<Double>
        get() = _maxX

    var _unit = MutableLiveData<String>()
    val unit : LiveData<String>
        get() = _unit

    var _running = MutableLiveData<Boolean>(true)
    val running : LiveData<Boolean>
        get() = _running

    val client = Client()

    fun loadUser(user: User){
        client.ip = user.ip
        client.port = user.port
    }

    init {
        client.subpage = "/measurements.php"
    }

    fun requestHelper(onSuccess: ((respond: String) -> Unit), message: String? = null){
        if(!requestIndicator) {
            requestIndicator = true
            viewModelScope.launch(Dispatchers.IO) {
                val respond = client.sendRequest(message)
                withContext(Dispatchers.Main) {
                    if (respond.first != null) {
                        if (respond.second) {
                            onSuccess(respond.first!!)
                        } else {
                            _makeAlert.value = Event(respond.first!!)
                            timer.stopTimer()
                        }
                    }
                    requestIndicator = false
                }
            }
        }
    }
    fun getServerData0(){
        if (currentName != null) {
            requestHelper(::getServerData)
        }
    }
    fun onSplitTypeChanged(radioGroup: RadioGroup?, id: Int) {
        k = 0
        signal1.resetData(arrayOf())
        currentName = getCurrentName(id)
    }
    fun getCurrentName(id: Int): String? {
        when(id){
            R.id.radioButton->return "temperature"
            R.id.radioButton2->return "pressure"
            R.id.radioButton3->return "humidity"
            R.id.radioButton4->return "roll"
            R.id.radioButton5->return "pitch"
            R.id.radioButton6->return "yaw"
        }
        return null
    }

    fun getServerData(respond: String){
        var json: JSONArray = JSONArray()
        try {
            json = JSONArray(respond)
        }catch (_: JSONException) { return }
        var value: Double? = null
        for (i in 0 until json.length()) {
            try {
                val item = json.getJSONObject(i)
                if (currentName == item.getString("name")) {
                    value = item.getString("value").toDouble()
                    _unit.value = item.getString("unit")
                    signal1.title = currentName
                }
            } catch (_: JSONException) { return }
        }
        if (k <= sampleMax && value != null) {
            val x = k * sampleTime
            signal1.appendData(DataPoint(x, value), false, sampleMax)
            k++

            if(x>10) {
                val newx = ceil((x+5)/5)*5
                _maxX.value = newx
                _minX.value = ceil(newx - 15.0)
            }else{
                _minX.value = 0.0
                _maxX.value = 15.0
            }
        }
    }




}