package com.example.air_app.table

import android.annotation.SuppressLint


class MeasurementViewModel(private val model: MeasurementModel) {
    val name: String?
        get() = model.mName
    val value: String
        @SuppressLint("DefaultLocale")
        get() = java.lang.String.format(valueFormat, model.mValue)
    val unit: String?
        get() = model.mUnit

    companion object {
        private const val valueFormat = "%.4f"
    }
}