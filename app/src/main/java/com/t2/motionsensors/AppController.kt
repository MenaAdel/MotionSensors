package com.t2.motionsensors

import android.app.Application
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.t2.motionsensors.domain.entity.DeviceDetails
import com.t2.motionsensors.domain.entity.ScreenSpecs
import java.security.AccessController.getContext

class AppController : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("Application", "Application created")
        val deviceDetails = DeviceDetails(
            timeStamp = System.currentTimeMillis(),
            deviceId = getDeviceIMEI(),
            userId = "",
            phoneOs = "android API ${Build.VERSION.SDK_INT}",
            deviceType = getDeviceName(),
            screenSpecs = ScreenSpecs(
                safeAreaPaddingBottom = 0,
                safeAreaPaddingTop = 0,
                height = pxToMm(resources.displayMetrics.heightPixels).toInt(),
                width = pxToMm(resources.displayMetrics.widthPixels).toInt(),
                diameter = getScreenDiameter()
            )
        )

        val jsonData = Gson().toJson(deviceDetails)
        Log.d("deviceData ", jsonData)

        Log.d("Application", "$deviceDetails")
    }


}