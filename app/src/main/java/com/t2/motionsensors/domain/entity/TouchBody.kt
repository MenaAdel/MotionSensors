package com.t2.motionsensors.domain.entity

import java.util.concurrent.CopyOnWriteArrayList

data class TouchBody(
    var user_id: String = "",
    var swipe: CopyOnWriteArrayList<Movement>? = null,
    var tap: CopyOnWriteArrayList<Movement>? = null,
)

data class Movement(
    val time_start: String,
    val time_stop: String,
    val data: List<Data>,
    val phone_orientation: Int,
)

data class Data(
    val dx: Float,
    val dy: Float,
    val moveX: Float,
    val moveY: Float,
    val vx: Float,
    val vy: Float,
    val x0: Float,
    val y0: Float,
    val fingerArea: Float,
    val pressure: Float,
    val time: Long
)