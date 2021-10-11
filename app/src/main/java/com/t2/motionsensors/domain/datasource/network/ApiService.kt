package com.t2.motionsensors.domain.datasource.network

import com.t2.motionsensors.domain.entity.SensorBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("")
    suspend fun addSensorValues(
        @Body body: SensorBody
    )
}