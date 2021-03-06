package com.t2.motionsensors.domain.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.t2.motionsensors.domain.datasource.repo.BiometricImp
import com.t2.motionsensors.domain.datasource.repo.IBiometric
import java.io.File
import java.util.concurrent.TimeUnit

class InfoDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val biometricRepo: IBiometric = BiometricImp()

    companion object {
        private const val USER_ID = "userId"
        private const val FILE_PATH = "filePath"
        fun startWorker(
            context: Context,
            userId: String,
            filePath: String
        ) {
            val sensorDataWorker =
                OneTimeWorkRequestBuilder<InfoDataWorker>()
            sensorDataWorker.setConstraints(
                Constraints.Builder().setRequiredNetworkType(
                    NetworkType.CONNECTED
                ).build()
            )

            val inputData = Data.Builder()
            inputData.apply {
                putString(USER_ID, userId)
                putString(FILE_PATH, filePath)
            }
            sensorDataWorker.setInputData(inputData.build())

            WorkManager.getInstance(context).enqueue(
                sensorDataWorker.build()
            )
        }
    }

    override suspend fun doWork(): Result {
        val file = File(inputData.getString(FILE_PATH).toString())
        val id = inputData.getString(USER_ID).toString()

        val touchApi =  biometricRepo.addTouchData(id ,file ,"info")

        return if (touchApi.status == 200) {
            Log.d("Worker" ,"Success sending")
            Result.success()
        }
        else {
            Log.d("Worker" ,"Fail sending: ${touchApi.message}")
            Result.failure()
        }
    }

}