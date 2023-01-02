package com.example.air_app.table

import com.example.air_app.iterator
import org.json.JSONException
import org.json.JSONObject

class MeasurementModel {
    var mName: String? = null
    var mData: String? = null


    constructor(name: String?, value: String?) {
        mName = name
        mData = value
    }

    constructor(key: String, data: JSONObject) {
        mData = ""
        var values = mutableListOf<String>()
        try {
            mName = key
            for (currentKey in data.keys()) {
                values.add(data.get(currentKey).toString())
            }
            mData = values.joinToString(" ")
        } catch (e: JSONException) {
            e.printStackTrace()
            throw JSONException("Json Object to Measurement Data parse error")
        }
    }

    fun toVM(): MeasurementViewModel {
        return MeasurementViewModel(this)
    }
}