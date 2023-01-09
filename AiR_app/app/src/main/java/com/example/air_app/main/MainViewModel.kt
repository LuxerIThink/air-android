package com.example.air_app.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.air_app.Event

class MainViewModel : ViewModel() {

    private val _eventNavigateToChart = MutableLiveData<Event<Boolean>>()
    val eventNavigateToChart: LiveData<Event<Boolean>>
        get() = _eventNavigateToChart

    private val _eventNavigateToLed = MutableLiveData<Event<Boolean>>()
    val eventNavigateToLed: LiveData<Event<Boolean>>
        get() = _eventNavigateToLed

    private val _eventNavigateToTable = MutableLiveData<Event<Boolean>>()
    val eventNavigateToTable : LiveData<Event<Boolean>>
        get() = _eventNavigateToTable

    fun onEventNavigateToChart() {
        _eventNavigateToChart.value = Event(true)
    }
    fun onEventNavigateToLed() {
        _eventNavigateToLed.value = Event(true)
    }
    fun onEventNavigateToTable() {
        _eventNavigateToTable.value = Event(true)
    }

}