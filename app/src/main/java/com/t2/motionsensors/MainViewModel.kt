package com.t2.motionsensors

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.t2.motionsensors.domain.datasource.storage.writeToFile
import com.t2.motionsensors.domain.entity.DeviceDetails
import com.t2.motionsensors.domain.entity.ScreenSpecs
import com.t2.motionsensors.domain.entity.SensorBody
import com.t2.motionsensors.domain.worker.InfoDataWorker
import com.t2.motionsensors.domain.worker.SensorDataWorker
import com.t2.motionsensors.domain.worker.TouchDataWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {


    fun addInfoModel(userId: String, accountId: String, context: Context) {
        val carrierName =
            (context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)?.networkOperatorName
                ?: "unknown"
        val deviceDetails = DeviceDetails(
            deviceId = context.requestPermission(),
            carrier = carrierName,
            userId = userId,
            phoneOS = "android API ${Build.VERSION.SDK_INT}",
            deviceType = getDeviceName(),
            screenSpecs = ScreenSpecs(
                safeAreaPaddingBottom = 0,
                safeAreaPaddingTop = 0,
                height = context.pxToMm(context.resources.displayMetrics.heightPixels).toInt(),
                width = context.pxToMm(context.resources.displayMetrics.widthPixels).toInt(),
                diameter = context.getScreenDiameter()
            )
        )

        addInfoData(userId, accountId, context, Gson().toJson(deviceDetails))
    }

    fun addSensorData(userId: String, accountId: String, context: Context, jsonData: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = context.writeToFile(jsonData, "sensor.json")
            val sensorBody = SensorBody(user_id = userId, account_id = accountId, file = filePath)
            SensorDataWorker.startWorker(
                context,
                sensorBody
            )
        }
    }

    fun addTouchData(userId: String, accountId: String, context: Context, jsonData: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = context.writeToFile(jsonData, "touch.json")
            TouchDataWorker.startWorker(
                context,
                userId,
                accountId = accountId,
                filePath.toString()
            )
        }
    }

    fun addInfoData(userId: String, accountId: String, context: Context, jsonData: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = context.writeToFile(jsonData, "info.json")
            InfoDataWorker.startWorker(
                context,
                userId,
                accountId = accountId,
                filePath.toString()
            )
        }
    }
}

