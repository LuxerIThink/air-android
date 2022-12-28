package com.example.air_app.table

import org.json.JSONException
import org.json.JSONObject

class MeasurementModel {
    var mName: String? = null
    var mValue: Double? = null
    var mUnit: String? = null

    constructor(name: String?, value: Double?, unit: String?) {
        mName = name
        mValue = value
        mUnit = unit
    }

    constructor(data: JSONObject) {
        try {
            mName = data.getString("name")
            mValue = data.getDouble("value")
            mUnit = data.getString("unit")
        } catch (e: JSONException) {
            e.printStackTrace()
            throw JSONException("Json Object to Measurement Data parse error")
        }
    }

    fun toVM(): MeasurementViewModel {
        return MeasurementViewModel(this)
    }
}