package com.example.air_app.table

class MeasurementViewModel(private val model: MeasurementModel) {
    val name: String?
        get() = model.mName
    val value: String?
        get() = model.mData
}