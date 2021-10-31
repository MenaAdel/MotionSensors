package com.t2.motionsensors.domain.datasource.network

import com.t2.motionsensors.domain.entity.ApiResponse
import com.t2.motionsensors.domain.entity.SensorBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("store_data")
    suspend fun addSensorValues(
        @Part("data_type") type: String? = null,
        @Part("user_id") userId: RequestBody,
        @Part file: MultipartBody.Part
    ): ApiResponse
}