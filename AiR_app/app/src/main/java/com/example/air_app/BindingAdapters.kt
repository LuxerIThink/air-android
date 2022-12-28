package com.example.air_app

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray
import org.json.JSONObject



fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}
fun <T> MutableLiveData<T>.postNotifyObserver() {
    this.postValue(this.value)
}

operator fun JSONArray.iterator(): Iterator<JSONObject>
        = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()

@BindingAdapter("android:visibility")
fun setVisibility(view: View, value: Boolean) {
    view.visibility = if (value) View.VISIBLE else View.GONE
}

@BindingAdapter("android:visibility")
fun setVisibility(view: View, value: Int) {
    when (value){
        0 -> view.visibility = View.GONE
        1 -> view.visibility = View.VISIBLE
        2 ->view.visibility = View.INVISIBLE
    }
}