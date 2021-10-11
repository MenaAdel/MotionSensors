package com.t2.motionsensors.domain.entity

data class SensorBody(
    var user_id: String = "${systemSecondTime()}",
    var accelerometer: List<Coordinates>? = null,
    var gyroscope: List<Coordinates>? = null,
    var magnetometer: List<Coordinates>? = null
)
fun systemSecondTime(): Long {
    return System.currentTimeMillis()
}

data class Coordinates(val x: Float?, val y: Float?, val z: Float?, val time: String)