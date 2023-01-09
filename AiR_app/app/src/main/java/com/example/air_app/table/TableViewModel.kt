package com.example.air_app.table

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.air_app.Event
import com.example.air_app.data.User
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class TableViewModel : ViewModel() {

    var adapter: MeasurementsAdapter? = null
        private set
    private var measurements: ArrayList<MeasurementViewModel>? = null
    private var server: ServerIoT? = null
    private val _makeAlert = MutableLiveData<Event<String>>()
    val makeAlert : LiveData<Event<String>>
        get() = _makeAlert

    fun loadUser(user: User){
        server!!.url = "http://${user.ip}:${user.port}/measurements.php"
    }

    fun Init(context: Context) {
        measurements = ArrayList<MeasurementViewModel>()
        adapter = MeasurementsAdapter(measurements)
        server = ServerIoT(context.applicationContext,
            object : VolleyResponseListener {
                override fun onError(message: String?) {
                    Log.d("Response error", message!!)
                    _makeAlert.value = Event(message)
                }

                override fun onResponse(response: JSONArray) {
                    var jsonObject = JSONObject()
                    try {
                        jsonObject = response.getJSONObject(0)
                    }catch (_: JSONException) { return }

                    val rs = jsonObject.length()
                    val ms = measurements!!.size
                    val sizeDiff = ms - rs
                    // remove redundant measurements from list
                    for (i in 0 until sizeDiff) {
                        measurements!!.removeAt(ms - 1 - i)
                        adapter!!.notifyItemRemoved(ms - 1 - i)
                    }
                    // iterate through JSON Array
                    for (i in 0 until rs) {
                        try {
                            /* get measurement model from JSON data */
                            val key = jsonObject.names()?.getString(i)
                            val measurement = MeasurementModel(key!!, jsonObject.getJSONObject(key))

                            /* update measurements list */if (i >= ms) {
                                measurements!!.add(measurement.toVM())
                                adapter!!.notifyItemInserted(i)
                            } else {
                                measurements!![i] = measurement.toVM()
                                adapter!!.notifyItemChanged(i)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        )
        server!!.url = "http://localhost:80/measurements.php"
    }


    fun updateMeasurements(v: View?) {
        server?.measurements
    }

}