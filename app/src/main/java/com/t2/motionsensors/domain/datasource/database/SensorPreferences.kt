package com.t2.motionsensors.domain.datasource.database

import android.content.Context
import android.content.SharedPreferences

const val PREF_NOTIFICATION_NAME = "com.t2.motionsensors"
const val USER_ID = "userIdValue"
const val ACCOUNT_ID = "accountIdValue"

class SensorPreferences(val context: Context) {

    private val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences(
            PREF_NOTIFICATION_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun saveValue(key: String, value: String) {
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getValue(key: String) =
        sharedPref.getString(key, "")
}