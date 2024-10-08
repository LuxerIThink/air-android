package com.example.air_app.led

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LedDisplayModel {
    val sizeX = 8
    val sizeY = 8
    private val model: Array<Array<LedModel?>> = Array<Array<LedModel?>>(sizeX) { arrayOfNulls<LedModel>(sizeY) }


    fun setLedModel(i: Int, j: Int, mdl: LedModel?) {
        model[i][j]?.setColor(mdl!!)
    }


    fun clearModel() {
        for (i in 0 until sizeX) {
            for (j in 0 until sizeY) {
                model[i][j]!!.clear()
            }
        }
    }

    private fun indexToJsonObject(x: Int, y: Int): JSONObject {
        val json = JSONObject()
        try {
            json.put("x", x)
            json.put("y", y)
            json.put("r", model[x][y]?.R ?: 0)
            json.put("g", model[x][y]?.G ?: 0)
            json.put("b", model[x][y]?.B ?: 0)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return json
    }

    val controlJsonArray: JSONArray
        get() {
            var ledN = 0
            val jsonArray = JSONArray()
            for (i in 0 until sizeX) {
                for (j in 0 until sizeY) {
                    if (model[i][j]!!.colorNotNull()) {
                        try {
                            jsonArray.put(ledN, indexToJsonObject(i, j))
                            ledN++
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            return jsonArray
        }


    init {
        for (x in 0 until sizeX) for (y in 0 until sizeY) model[x][y] = LedModel()
    }
}