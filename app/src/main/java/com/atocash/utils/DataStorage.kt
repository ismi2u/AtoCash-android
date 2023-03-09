package com.atocash.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by geniuS on 9/4/2019.
 */
class DataStorage(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(Keys.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveString(key: String?, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun saveInt(key: String?, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun saveBoolean(key: String?, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun isDataAvailable(key: String?): Boolean {
        return sharedPreferences.contains(key)
    }

    fun getInt(key: String?): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun getString(key: String?): String? {
        return sharedPreferences.getString(key, "")
    }

    fun getBoolean(key: String?): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun removeAllData() {
        sharedPreferences.edit().clear().apply()
    }

    fun removeData(key: String?) {
        if (isDataAvailable(key)) {
            sharedPreferences.edit().remove(key).apply()
        }
    }

    fun saveArrayListAsString(key: String?, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getArrayListString(key: String?): String? {
        return sharedPreferences.getString(key, "")
    }
}