package com.example.air_app.table

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class ServerIoT(context: Context, volleyResponseListener: VolleyResponseListener) {

    var url: String? = null
    private val listener: VolleyResponseListener
    private val queue: RequestQueue

    fun getListener(): VolleyResponseListener {
        return listener
    }

    val measurements: Unit
        get() {
            val request = JsonArrayRequest(
                Request.Method.GET, url, null,
                { response ->
                    listener.onResponse(response)
                }
            ) { error ->
                val msg = error.message
                if (msg != null) listener.onError(msg) else listener.onError("UNKNOWN ERROR")
            }

            queue.add(request)
        }

    init {
        queue = Volley.newRequestQueue(context.applicationContext)
        listener = volleyResponseListener
    }
}