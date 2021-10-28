package com.t2.motionsensors.domain.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.t2.motionsensors.domain.datasource.repo.BiometricImp
import com.t2.motionsensors.domain.datasource.repo.IBiometric
import com.t2.motionsensors.domain.entity.SensorBody
import java.io.File
import java.util.concurrent.TimeUnit

class SensorDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val biometricRepo: IBiometric = BiometricImp()

    companion object {
        private const val SENSOR_BODY = "sensorBody"
        fun startWorker(
            context: Context,
            sensorBody: SensorBody,
        ) {
            val sensorDataWorker =
                PeriodicWorkRequestBuilder<SensorDataWorker>(5, TimeUnit.MINUTES)
            sensorDataWorker.setConstraints(
                Constraints.Builder().setRequiredNetworkType(
                    NetworkType.CONNECTED
                ).build()
            )

            val inputData = Data.Builder()
            inputData.apply {
                putString(SENSOR_BODY, Gson().toJson(sensorBody))
            }
            sensorDataWorker.setInputData(inputData.build())

            WorkManager.getInstance(context).enqueue(
                sensorDataWorker.build()
            )
        }
    }

    override suspend fun doWork(): Result {
        val sensorBody: SensorBody? = Gson().fromJson(inputData.getString(SENSOR_BODY),
            SensorBody::class.java)

        val sensorApi = sensorBody?.let { biometricRepo.addSensorData(it) }

        return if (sensorApi?.status == 200) {
            Log.d("Worker" ,"Success sending")
            Result.success()
        }
        else {
            Log.d("Worker" ,"Fail sending: ${sensorApi?.message}")
            Result.failure()
        }
    }

}