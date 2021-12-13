package com.t2.motionsensors

interface SensorListener {
    fun onApiValueChanged(response: String)
}