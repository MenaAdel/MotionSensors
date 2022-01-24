package com.t2.motionsensors.domain.entity

data class DeviceDetails(
    val carrier: String = "",
    val deviceId: String = "",
    val userId: String = "",
    val phoneOS: String ="",
    val deviceType: String = "",
    val screenSpecs: ScreenSpecs
)

data class ScreenSpecs(
    val safeAreaPaddingTop: Int = 0,
    val safeAreaPaddingBottom: Int = 0,
    val width: Int = 0,
    val height: Int = 0,
    val diameter: Double = 0.0
)