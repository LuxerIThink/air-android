package com.example.air_app.client

import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class Client {
    var ip: String = ""
    var method: String = "GET"
    var port: Int = 80
    var subpage: String = ""
    var message: String = ""

    fun sendRequest(message: String? = null): Pair<String?,Boolean> {
        val url = URL("http", ip, port, subpage)
        var response: String?
        var success = false
        try {
            val conn = url.openConnection() as HttpURLConnection

            conn.requestMethod = method
            conn.connectTimeout = 2000
            conn.connect()
            if (message != null) {
                try {
                    DataOutputStream(conn.outputStream).use { it.writeBytes(message) }
                } catch (_: IOException) { }
            }
            when (conn.responseCode) {
                200 -> {
                    try {
                        BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
                            response = ""
                            br.forEachLine {
                                response += (it + "\n")
                            }
                        }
                        success = true
                    } catch (e: IOException) {
                        response = e.message
                    }
                }
                401 -> {
                    response = "Error 401 (incorrect login or password)"
                }
                else -> {
                    response = "Response code: " + conn.responseCode.toString()
                }
            }
            conn.disconnect()
        }catch (e: java.net.SocketTimeoutException) {
            response = e.message
        }catch (e: IOException){
            Log.i("ClientError", e.toString())
            response = e.message
        }
        return Pair(response, success)
    }
}