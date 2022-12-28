/**
 *
 * @file    LED Display Control Example/Model/LedDisplayModel.java
 * @author  Adrian Wojcik  adrian.wojcik@put.poznan.pl
 * @version V2.0
 * @date    10-May-2021
 * @brief   LED display controller: LED display data model - matrix of LEDs
 */
package com.example.air_app.led

import org.json.JSONArray
import org.json.JSONException

class LedDisplayModel {
    val sizeX = 8 //!< Display horizontal size
    val sizeY = 8 //!< Display vertical size
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

    private fun indexToJsonArray(x: Int, y: Int): JSONArray {
        val array = JSONArray()
        try {
            array.put(0, x)
            array.put(1, y)
            array.put(2, model[x][y]?.R ?: 0)
            array.put(3, model[x][y]?.G ?: 0)
            array.put(4, model[x][y]?.B ?: 0)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return array
    }

    val controlJsonArray: JSONArray
        get() {
            var led_n = 0
            val jsonArray = JSONArray()
            for (i in 0 until sizeX) {
                for (j in 0 until sizeY) {
                    if (model[i][j]!!.colorNotNull()) {
                        try {
                            jsonArray.put(led_n, indexToJsonArray(i, j))
                            led_n++
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