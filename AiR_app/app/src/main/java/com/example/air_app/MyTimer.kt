/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.air_app

import android.os.Handler
import android.os.Looper

class MyTimer (private var Tp: Long = 1000, private var foo: (() -> Unit)? = null){

    private var counter = 0
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var _running = false
    val running: Boolean
    get() = _running


    fun startTimer() {
        _running = true
        runnable = Runnable {
            counter++
            foo?.invoke()
            handler.postDelayed(runnable, Tp)
        }

        handler.postDelayed(runnable, Tp)

    }

    fun stopTimer() {
        _running = false
        handler.removeCallbacks(runnable)
    }
}