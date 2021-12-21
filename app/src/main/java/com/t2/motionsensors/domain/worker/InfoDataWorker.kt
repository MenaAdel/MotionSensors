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
        private const val ACCOUNT_ID = "accountId"
        private const val FILE_PATH = "filePath"
        const val OUTPUT_KEY_INFO = "outputKeyInfo"
        fun startWorker(
            context: Context,
            userId: String,
            accountId: String,
            filePath: String,
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
                putString(ACCOUNT_ID, accountId)
            }
            sensorDataWorker.setInputData(inputData.build())

            WorkManager.getInstance(context)
                .enqueueUniqueWork("InfoDataWorker", ExistingWorkPolicy.KEEP,
                    sensorDataWorker.build()
                )
        }
    }

    override suspend fun doWork(): Result {
        val file = File(inputData.getString(FILE_PATH).toString())
        val id = inputData.getString(USER_ID).toString()
        val accountId = inputData.getString(ACCOUNT_ID).toString()

        val touchApi = biometricRepo.addTouchData(accountId ,id, file, "info")

        return if (touchApi.status == 200) {
            Log.d("Worker", "Success sending info data")
            val outputData = createOutputData("Success sending info data")
            Result.success(outputData)
        } else {
            Log.d("Worker", "Fail sending info: ${touchApi.message}")
            val outputData = createOutputData("Fail sending info: ${touchApi.message}")
            Result.failure(outputData)
        }
    }
}

fun createOutputData(outputData: String ,key: String = InfoDataWorker.OUTPUT_KEY_INFO): Data {
    return Data.Builder()
        .putString(key, outputData)
        .build()
}