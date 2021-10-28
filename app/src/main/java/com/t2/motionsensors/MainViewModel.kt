package com.t2.motionsensors

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t2.motionsensors.domain.datasource.storage.writeToFile
import com.t2.motionsensors.domain.entity.SensorBody
import com.t2.motionsensors.domain.worker.SensorDataWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    suspend fun addSensorData(context: Context, jsonData: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = context.writeToFile(jsonData, "sensor_${System.currentTimeMillis()}.json")
            val sensorBody = SensorBody(file = filePath)
            SensorDataWorker.startWorker(
                context,
                sensorBody
            )
        }
    }
}

