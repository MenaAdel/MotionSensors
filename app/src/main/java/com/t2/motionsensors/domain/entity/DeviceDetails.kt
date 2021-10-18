package com.t2.motionsensors.domain.entity

data class DeviceDetails(
    val timeStamp: Long,
    val deviceId: String,
    val userId: String,
    val phoneOs: String,
    val deviceType: String,
    val screenSpecs: ScreenSpecs
)

data class ScreenSpecs(
    val safeAreaPaddingTop: Int,
    val safeAreaPaddingBottom: Int,
    val width: Int,
    val height: Int,
    val diameter: Double
)