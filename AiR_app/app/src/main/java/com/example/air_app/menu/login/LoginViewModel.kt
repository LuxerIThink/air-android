package com.example.air_app.menu.login


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.air_app.Event
import com.example.air_app.data.User

class LoginViewModel: ViewModel() {

    var loaded: Boolean = false
    var ip = MutableLiveData<String>()
    var port = MutableLiveData<String>()

    private val _eventConfirm = MutableLiveData<Event<Boolean>>()
    val eventConfirm : LiveData<Event<Boolean>>
        get() = _eventConfirm

    fun loadUserData(user: User){
        ip.postValue(user.ip)
        port.postValue(user.port.toString())
        loaded = true
    }

    fun onEventConfirm() {
        _eventConfirm.value = Event(true)
    }

    fun deleteUser(){
        ip.value = ""
        port.value = ""
    }
}