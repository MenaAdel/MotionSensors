package com.t2.motionsensors.domain.datasource.repo

import com.t2.motionsensors.domain.entity.ApiResponse
import com.t2.motionsensors.domain.entity.SensorBody
import java.io.File

interface IBiometric {
    suspend fun addSensorData(body: SensorBody): ApiResponse
    suspend fun addTouchData(accountId: String? ,user_id: String ,file: File ,type: String): ApiResponse
}