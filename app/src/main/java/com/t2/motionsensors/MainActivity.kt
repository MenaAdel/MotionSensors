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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.t2.motionsensors.databinding.ActivityMainBinding
import com.t2.motionsensors.domain.entity.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding

    private var accelerometer: Sensor? = null
    private var accelerometerUncalibrated: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null
    private var rotation: Sensor? = null
    private val sensorData = SensorBody()
    private val accelerometerArray = mutableListOf<Coordinates>()
    private val gyroscopeArray = mutableListOf<Coordinates>()
    private val magnetometerArray = mutableListOf<Coordinates>()
    private val deviceMotionArray = mutableListOf<DeviceMotion>()
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
    private var tap: MutableList<Movement> = mutableListOf()
    private var swipe: MutableList<Movement> = mutableListOf()
    private var touchData: MutableList<Data> = mutableListOf()
    private var touchSwipeData: MutableList<Data> = mutableListOf()
    private var touchBody: TouchBody? = null
    private val dateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-mm-dd HH:mm:ss:SSS",
            Locale.getDefault())
    }
    private var isActionMove = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecycler()
        setupSensors()
        touchBody = TouchBody(user_id = "test", swipe = swipe, tap = tap)
    }

    private fun initRecycler() {
        val data = ArrayList<ItemsViewModel>()

        for (i in 1..20) {
            data.add(ItemsViewModel(R.drawable.ic_launcher_background, "Item $i"))
        }
        with(binding.recyclerview) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = CustomAdapter(data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSensors() {
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // accelerometer
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: Log.d(TAG, "accelerometer not supported")

        //gyroscope
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: Log.d(TAG, "gyroscope not supported")

        //magnetometer
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: Log.d(TAG, "magnetometer not supported")

        //accelerometerUncalibrated
        accelerometerUncalibrated =
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED)
        accelerometerUncalibrated?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: Log.d(TAG, "accelerometerUncalibrated not supported")

        //rotation
        rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        rotation?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: Log.d(TAG, "rotation not supported")
    }

    companion object {
        private const val TAG = "MainActivityScreen"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onSensorChanged(event: SensorEvent?) {
        Log.d(TAG, "sensor type is: ${event?.sensor?.type}")
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
                deviceMotionObject.apply {
                    accelerationIncludingGravity = CoordinatesDevice(
                        x = event.values?.get(0),
                        y = event.values?.get(1),
                        z = event.values?.get(2)
                    )
                    orientation = getDeviceOrientation()

                }
                Log.d(
                    TAG,
                    "onSensorChanged x: ${event.values?.get(0)} y: ${event.values?.get(1)} z: ${
                        event.values?.get(2)
                    }"
                )
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
                Log.d(
                    TAG,
                    "onSensorChanged x: ${event.values?.get(0)} y: ${event.values?.get(1)} z: ${
                        event.values?.get(2)
                    }"
                )

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
                Log.d(
                    TAG,
                    "onSensorChanged x: ${event.values?.get(0)} y: ${event.values?.get(1)} z: ${
                        event.values?.get(2)
                    }"
                )
            }
            Sensor.TYPE_ACCELEROMETER_UNCALIBRATED -> {
                deviceMotionObject.acceleration = CoordinatesDevice(
                    x = event.values?.get(0),
                    y = event.values?.get(1),
                    z = event.values?.get(2)
                )
            }
            Sensor.TYPE_ROTATION_VECTOR -> {
                deviceMotionObject.apply {
                    rotation = Rotation(
                        alpha = event.values?.get(0),
                        beta = event.values?.get(1),
                        gamma = event.values?.get(2)
                    )
                    time = dateFormat.format(Date())
                }
            }
            else -> {
            }
        }

        lifecycleScope.launch { fillSensorData() }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private suspend fun fillSensorData() {
        delay(10000)
        deviceMotionArray.add(index, deviceMotionObject)
        deviceMotionObject = DeviceMotion()
        index++
        delay(60000)
        sensorData.apply {
            accelerometer = accelerometerArray
            gyroscope = gyroscopeArray
            magnetometer = magnetometerArray
            deviceMotion = deviceMotionArray
        }
        val jsonData = Gson().toJson(sensorData)
        // Log.d("json data is: ", jsonData)
    }

    private fun getDeviceOrientation(): Int {
        return if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            1
        } else {
            0
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "finger area is ${event?.size}")
        Log.d(TAG, "pressure is ${event?.pressure}")
        for (i in 0 until (event?.pointerCount ?: 0)){
            Log.d(TAG, "size of pointerCount $i is ${event?.getSize(i)}")
        }
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
            else -> {
            }
        }
        return true
    }

    private fun onEventUp(event: MotionEvent) {
        endX = event.x
        endY = event.y
        endTime = System.currentTimeMillis()
        Log.d(TAG, "endPoint is ${event.x} ,${event.y}")
        Log.d(TAG, "distance x is ${endX - startX}")
        Log.d(TAG, "distance y is ${endY - startY}")
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
            touchData.add(data)
            fillTouchSwipe()
        } else {
            touchSwipeData.add(data)
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
        val jsonData = Gson().toJson(touchBody)
        Log.d("json touch body is: ", jsonData)
    }

    private fun fillTouchSwipe() {
        val movement = Movement(time_start = dateFormat.format(startTime),
            time_stop = dateFormat.format(endTime),
            data = touchSwipeData,
            phone_orientation = getDeviceOrientation())
        swipe.add(movement)
        val jsonData = Gson().toJson(touchBody)
        Log.d("json touch body is: ", jsonData)
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
}