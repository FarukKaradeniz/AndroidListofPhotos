package com.farukkaradeniz.androidlistofphotos.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log

/**
 * Created by Faruk Karadeniz on 7.05.2018.
 * Twitter: twitter.com/Omeerfk
 * Github: github.com/FarukKaradeniz
 * LinkedIn: linkedin.com/in/FarukKaradeniz
 * Website: farukkaradeniz.com
 */
fun Context.isPermissionGranted(permission: String) =
        ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.requestPermission(array: Array<String>, requestId: Int) =
        ActivityCompat.requestPermissions(this as AppCompatActivity, array, requestId)

fun Context.logi(message: String) {
    Log.i(this.javaClass.simpleName, message)
}

fun Context.loge(message: String) {
    Log.e(this.javaClass.simpleName, message)
}