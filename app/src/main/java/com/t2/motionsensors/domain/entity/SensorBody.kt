package com.t2.motionsensors.domain.entity

data class SensorBody(
    var user_id: String = "${systemSecondTime()}",
    var accelerometer: List<Coordinates>? = null,
    var gyroscope: List<Coordinates>? = null,
    var magnetometer: List<Coordinates>? = null,
    var deviceMotion: List<DeviceMotion>? = null
)

fun systemSecondTime(): Long {
    return System.currentTimeMillis()
}

data class Coordinates(val x: Float?, val y: Float?, val z: Float?, val time: String)
data class CoordinatesDevice(val x: Float?, val y: Float?, val z: Float?)
data class DeviceMotion(
    var accelerationIncludingGravity: CoordinatesDevice? = null,
    var rotation: Rotation? = null,
    var acceleration: CoordinatesDevice? = null,
    var orientation: Int = 0,
    var time: String? = ""
)

data class Rotation(val gamma: Float?, val alpha: Float?, val beta: Float?)