package com.t2.motionsensors.domain.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class SensorDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        fun startWorker(
            context: Context,

        ) {
            val sensorDataWorker =
                PeriodicWorkRequestBuilder<SensorDataWorker>(5 ,TimeUnit.MINUTES)
            sensorDataWorker.setConstraints(
                Constraints.Builder().setRequiredNetworkType(
                    NetworkType.CONNECTED
                ).build()
            )

            val inputData = Data.Builder()
            inputData.apply {
            }
            sensorDataWorker.setInputData(inputData.build())

            WorkManager.getInstance(context).enqueue(
                sensorDataWorker.build()
            )
        }
    }
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }

}