package com.t2.motionsensors

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Build.*
import android.provider.Settings
import android.telephony.TelephonyManager
import java.util.*
import android.util.TypedValue

import android.util.DisplayMetrics
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.math.pow
import kotlin.math.sqrt


@SuppressLint("HardwareIds")
fun Context.getDeviceIMEI(): String {
    return if (Build.VERSION.SDK_INT >= VERSION_CODES.Q) {
        Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID)
    } else {
        val ts: String = Context.TELEPHONY_SERVICE
        val mTelephonyMgr = getSystemService(ts) as TelephonyManager?
        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            mTelephonyMgr?.imei.toString()
        } else {
            ""
        }
    }
}

fun getDeviceName(): String =
    if (MODEL.startsWith(MANUFACTURER, ignoreCase = true)) {
        MODEL
    } else {
        "$MANUFACTURER $MODEL"
    }.capitalize(Locale.ROOT)

fun Context.pxToMm(px: Int): Float {
    val dm = resources.displayMetrics
    return px / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1f, dm)
}

fun Context.getScreenDiameter(): Double {
    val dm = resources.displayMetrics
    val width = dm.widthPixels
    val height = dm.heightPixels
    val x = width.toDouble().pow(2.0)
    val y = height.toDouble().pow(2.0)
    return sqrt(x + y)
}

fun systemSecondTime(): Long {
    return System.currentTimeMillis()
}
