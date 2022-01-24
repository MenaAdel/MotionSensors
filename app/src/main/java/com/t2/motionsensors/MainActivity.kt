package com.t2.motionsensors

import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.work.WorkManager
import com.google.gson.Gson
import com.t2.motionsensors.databinding.ActivityMainBinding
import com.t2.motionsensors.domain.datasource.storage.writeToFileOnDisk
import com.t2.motionsensors.domain.entity.*
import com.t2.motionsensors.presentation.EndSessionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity(), SensorEventListener, EndSessionListener {

    var navController: NavController? = null
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val sensorFlow: MutableSharedFlow<SensorEvent> = MutableSharedFlow()
    private val gravity: FloatArray = FloatArray(3)
    private var accelerometer: Sensor? = null
    private var accelerometerUncalibrated: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null
    private var rotation: Sensor? = null
    private var sensorData = FileData()
    private val accelerometerArray = CopyOnWriteArrayList<Coordinates>()
    private val gyroscopeArray = CopyOnWriteArrayList<Coordinates>()
    private val magnetometerArray = CopyOnWriteArrayList<Coordinates>()
    private val deviceMotionArray = CopyOnWriteArrayList<DeviceMotion>()
    private var deviceMotionObject: DeviceMotion = DeviceMotion()
    private var index = 0
    private var startX: Float = 0f
    private var endX: Float = 0f
    private var startY: Float = 0f
    private var endY: Float = 0f
    private var moveX: Float = 0f
    private var moveY: Float = 0f
    private var startTime: Long = 0L
    private var endTime: Long = 0L
    private var tap: CopyOnWriteArrayList<Movement> = CopyOnWriteArrayList()
    private var swipe: CopyOnWriteArrayList<Movement> = CopyOnWriteArrayList()
    private var touchData: MutableList<Data> = mutableListOf()
    private var touchSwipeData: MutableList<Data> = mutableListOf()
    private var touchBody: TouchBody? = null
    private val dateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS",
            Locale("es", "ES"))
    }
    private var isActionMove = false
    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.nav_host_fragment)
        showPermissionDialog()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setupSensors()
        touchBody = TouchBody(user_id = Users.userId, swipe = swipe, tap = tap)
        timer= Timer()
        timerTask= object :TimerTask(){
            override fun run() {
                CoroutineScope(Dispatchers.IO).launch {
                    if (Users.userId.isNotEmpty()) {
                        fillSensorData()
                    } else {
                        setSensorDataEmpty()
                    }
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask,30000,30000)
        lifecycleScope.launch {
            sensorFlow.collectLatest {
                when (it.sensor.type) {
                    Sensor.TYPE_LINEAR_ACCELERATION -> {
                        deviceMotionObject.acceleration = CoordinatesDevice(
                            x = it.values?.get(0),
                            y = it.values?.get(1),
                            z = it.values?.get(2)
                        )
                    }
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        deviceMotionObject.apply {
                            rotation = Rotation(
                                alpha = it.values?.get(0),
                                beta = it.values?.get(1),
                                gamma = it.values?.get(2)
                            )
                            time = dateFormat.format(Date())
                            orientation = getDeviceOrientation()
                        }
                    }
                    Sensor.TYPE_ACCELEROMETER -> {
                        val alpha = 0.8f

                        gravity[0] = alpha * gravity[0] + (1 - alpha) * it.values[0]
                        gravity[1] = alpha * gravity[1] + (1 - alpha) * it.values[1]
                        gravity[2] = alpha * gravity[2] + (1 - alpha) * it.values[2]

                        deviceMotionObject.apply {
                            accelerationIncludingGravity = CoordinatesDevice(
                                x = it.values?.get(0),
                                y = it.values?.get(1),
                                z = it.values?.get(2)
                            )
                            acceleration = CoordinatesDevice(
                                x = it.values[0] - gravity[0],
                                y = it.values[1] - gravity[1],
                                z = it.values[2] - gravity[2]
                            )
                        }
                    }
                }
            }
        }
    }

    private fun sendInfoData() {
        if (Users.isFirstTime) {
            viewModel.addInfoModel(Users.userId,
                Users.accountId,
                this)
            Users.isFirstTime = false
        }
    }

    private fun setSensorDataEmpty() {
        sensorData = FileData()
        accelerometerArray.clear()
        gyroscopeArray.clear()
        magnetometerArray.clear()
        deviceMotionArray.clear()
        index = 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSensors() {
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // accelerometer
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, 10000)
        } ?: Log.d(TAG, "accelerometer not supported")

        //gyroscope
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        gyroscope?.let {
            sensorManager.registerListener(this, it, 10000)
        } ?: Log.d(TAG, "gyroscope not supported")

        //magnetometer
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        magnetometer?.let {
            sensorManager.registerListener(this, it, 10000)
        } ?: Log.d(TAG, "magnetometer not supported")

        //accelerometerUncalibrated
        accelerometerUncalibrated =
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED)
        accelerometerUncalibrated?.let {
            sensorManager.registerListener(this, it, 10000)
        } ?: Log.d(TAG, "accelerometerUncalibrated not supported")

        //rotation
        rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        rotation?.let {
            sensorManager.registerListener(this, it, 10000)
        } ?: Log.d(TAG, "rotation not supported")
    }

    companion object {
        private const val TAG = "MainActivityScreen"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onSensorChanged(event: SensorEvent?) {
        //Log.d(TAG, "sensor type is: ${event?.sensor?.type}")

        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelerometerArray.add(
                    Coordinates(
                        x = event.values?.get(0),
                        y = event.values?.get(1),
                        z = event.values?.get(2),
                        time = dateFormat.format(Date())
                    )
                )
                lifecycleScope.launch { sensorFlow.emit(event) }

                /*Log.d(
                    TAG,
                    "onSensorChanged x: ${event.values?.get(0)} y: ${event.values?.get(1)} z: ${
                        event.values?.get(2)
                    }"
                )*/
            }
            Sensor.TYPE_GYROSCOPE -> {
                gyroscopeArray.add(
                    Coordinates(
                        x = event.values?.get(0),
                        y = event.values?.get(1),
                        z = event.values?.get(2),
                        time = dateFormat.format(Date())
                    )
                )
                /*Log.d(
                    TAG,
                    "onSensorChanged x: ${event.values?.get(0)} y: ${event.values?.get(1)} z: ${
                        event.values?.get(2)
                    }"
                )*/

            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                magnetometerArray.add(
                    Coordinates(
                        x = event.values?.get(0),
                        y = event.values?.get(1),
                        z = event.values?.get(2),
                        time = dateFormat.format(Date())
                    )
                )
                /*Log.d(
                    TAG,
                    "onSensorChanged x: ${event.values?.get(0)} y: ${event.values?.get(1)} z: ${
                        event.values?.get(2)
                    }"
                )*/
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                lifecycleScope.launch { sensorFlow.emit(event) }
            }
            Sensor.TYPE_ROTATION_VECTOR -> {
                lifecycleScope.launch { sensorFlow.emit(event) }
            }
            else -> {
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun fillSensorData() {
        touchBody?.user_id = Users.userId
        sendInfoData()
        //Log.d("fillS", "${deviceMotionObject.isNotEmpty()}")
        if (deviceMotionObject.isNotEmpty()) {
            deviceMotionArray.add(index, deviceMotionObject)
            deviceMotionObject = DeviceMotion()
            index++
        }

        sensorData.apply {
            user_id = Users.userId
            accelerometer = accelerometerArray
            gyroscope = gyroscopeArray
            magnetometer = magnetometerArray
            deviceMotion = deviceMotionArray
        }
        val jsonData = Gson().toJson(sensorData)
        val jsonTouchData = Gson().toJson(touchBody)
        viewModel.addSensorData(userId = Users.userId,
            accountId = Users.accountId, this, jsonData)
        viewModel.addTouchData(userId = Users.userId,
            accountId = Users.accountId, this, jsonTouchData)
        //writeToFileOnDisk(jsonData ,"sensor_${dateFormat.format(Date())}.json")
        setSensorDataEmpty()

    }

    private fun getDeviceOrientation(): Int {
        return if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            1
        } else {
            0
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        //Log.d(TAG, "finger area is ${event?.size}")
        //Log.d(TAG, "pressure is ${event?.pressure}")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                startTime = System.currentTimeMillis()
                Log.d(TAG, "startPoint is ${event.x} ,${event.y}")
            }
            MotionEvent.ACTION_MOVE -> {
                isActionMove = true
                moveX = event.x
                moveY = event.y
            }
            MotionEvent.ACTION_UP -> {
                onEventUp(event)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun onEventUp(event: MotionEvent) {
        endX = event.x
        endY = event.y
        endTime = System.currentTimeMillis()
        //Log.d(TAG, "endPoint is ${event.x} ,${event.y}")
        //Log.d(TAG, "distance x is ${endX - startX}")
        //Log.d(TAG, "distance y is ${endY - startY}")
        val xVelocity = calculateVelocity(startX, endX, (endTime - startTime).toFloat())
        val yVelocity = calculateVelocity(startY, endY, (endTime - startTime).toFloat())
        val data = Data(dx = endX - startX,
            dy = endY - startY,
            moveX = moveX,
            moveY = moveY,
            vx = xVelocity,
            vy = yVelocity,
            x0 = startX,
            y0 = startY,
            fingerArea = event.size,
            pressure = event.pressure,
            time = endTime - startTime)

        if (isActionMove) {
            touchSwipeData.add(data)
            fillTouchSwipe()
        } else {
            touchData.add(data)
            fillTouchData()
        }
        isActionMove = false
        resetData()
    }

    private fun fillTouchData() {
        val movement = Movement(time_start = dateFormat.format(startTime),
            time_stop = dateFormat.format(endTime),
            data = touchData,
            phone_orientation = getDeviceOrientation())
        tap.add(movement)
        //val jsonData = Gson().toJson(touchBody)
        //Log.d("jsonto ", jsonData)
    }

    private fun fillTouchSwipe() {
        val movement = Movement(time_start = dateFormat.format(startTime),
            time_stop = dateFormat.format(endTime),
            data = touchSwipeData,
            phone_orientation = getDeviceOrientation())
        swipe.add(movement)
        //val jsonData = Gson().toJson(touchBody)
       // Log.d("json touch body is: ", jsonData)
    }

    private fun resetData() {
        startX = 0f
        endX = 0f
        startY = 0f
        endY = 0f
        moveX = 0f
        moveY = 0f
        startTime = 0L
        endTime = 0L
    }

    private fun calculateVelocity(
        startDistance: Float,
        endDistance: Float,
        duration: Float,
    ): Float {
        val distance = abs(endDistance - startDistance)
        return distance / duration
    }

    override fun onEndSession() {
        fillSensorData()
        Users.apply {
            userId = ""
            accountId = null
        }
    }

    override fun onDestroy() {
        endWorker()
        super.onDestroy()
    }
    private fun endWorker(){
        WorkManager.getInstance(this).cancelUniqueWork("InfoDataWorker")
        WorkManager.getInstance(this).cancelUniqueWork("SensorDataWorker")
        WorkManager.getInstance(this).cancelUniqueWork("TouchDataWorker")
    }
}