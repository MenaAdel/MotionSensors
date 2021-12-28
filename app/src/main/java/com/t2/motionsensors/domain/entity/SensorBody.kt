package com.t2.motionsensors.domain.entity

import com.t2.motionsensors.systemSecondTime
import java.util.concurrent.CopyOnWriteArrayList

data class SensorBody(
    var user_id: String = "${systemSecondTime()}",
    var account_id: String = "${systemSecondTime()}",
    var file: String? = null
)

data class FileData(
    var user_id: String = "${systemSecondTime()}",
    var accelerometer: CopyOnWriteArrayList<Coordinates>? = null,
    var gyroscope: CopyOnWriteArrayList<Coordinates>? = null,
    var magnetometer: CopyOnWriteArrayList<Coordinates>? = null,
    var deviceMotion: CopyOnWriteArrayList<DeviceMotion>? = null
)

data class Coordinates(val x: Float?, val y: Float?, val z: Float?, val time: String)
data class CoordinatesDevice(val x: Float?, val y: Float?, val z: Float?)
data class DeviceMotion(
    var accelerationIncludingGravity: CoordinatesDevice? = null,
    var rotation: Rotation? = null,
    var acceleration: CoordinatesDevice? = null,
    var orientation: Int = 0,
    var time: String? = ""
) {
    fun isNotEmpty(): Boolean {
        return accelerationIncludingGravity != null && rotation != null && acceleration != null
    }
}

data class Rotation(val gamma: Float?, val alpha: Float?, val beta: Float?)