package com.example.air_app.led

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.air_app.Event
import com.example.air_app.data.User
import com.example.air_app.server.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LedViewModel : ViewModel() {
    val client = Client()
    var progressIndicator = MutableLiveData(false)

    private val _makeAlert = MutableLiveData<Event<String>>()
    val makeAlert : LiveData<Event<String>>
        get() = _makeAlert

    fun loadUser(user: User){
        client.ip = user.ip
        client.port = user.port
    }

    init {
        client.subpage = "led_display.php"
    }

    fun requestHelper(onSuccess: ((respond: String) -> Unit), message: String? = null){
        if (progressIndicator.value == false) {
            progressIndicator.value = true
            viewModelScope.launch(Dispatchers.IO) {
                val respond = client.sendRequest(message)
                withContext(Dispatchers.Main) {
                    if (respond.first != null) {
                        if (respond.second) {
                            onSuccess(respond.first!!)
                        }else{
                            _makeAlert.value = Event(respond.first!!)
                        }
                    }
                    progressIndicator.value = false
                }
            }
        }
    }
}