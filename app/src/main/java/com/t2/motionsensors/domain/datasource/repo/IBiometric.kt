package com.t2.motionsensors.domain.datasource.repo

import com.t2.motionsensors.domain.entity.ApiResponse
import com.t2.motionsensors.domain.entity.SensorBody

interface IBiometric {
    suspend fun addSensorData(body: SensorBody): ApiResponse
}