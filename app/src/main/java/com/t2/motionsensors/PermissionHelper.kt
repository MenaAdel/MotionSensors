package com.t2.motionsensors

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialDialogs
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.PermissionDeniedResponse

import com.karumi.dexter.listener.PermissionGrantedResponse

import com.karumi.dexter.listener.single.PermissionListener
import android.R
import com.karumi.dexter.listener.single.CompositePermissionListener

import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener

fun Context.requestPermission(){
    Dexter.withContext(this)
        .withPermission(Manifest.permission.READ_PHONE_STATE)
        .withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest,
                token: PermissionToken,
            ) { /* ... */
            }
        }).check()
}

fun Context.showPermissionDialog() {
    val dialogPermissionListener: PermissionListener = DialogOnDeniedPermissionListener.Builder
        .withContext(this)
        .withTitle("Phone permission")
        .withMessage("Phone permission is needed to get device id")
        .withButtonText(R.string.ok)
        .build()
}