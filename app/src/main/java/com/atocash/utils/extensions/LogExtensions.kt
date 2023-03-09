package com.atocash.utils.extensions

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.atocash.BuildConfig
import com.atocash.R
import com.atocash.network.response.BaseResponse
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder


fun printLog(message: String) {
    if (BuildConfig.DEBUG) {
        val chunkSize = 2048
        if (message.length >= chunkSize) {
            var i = 0
            while (i < message.length) {
                Log.e(
                    "Debug ",
                    message.substring(i, message.length.coerceAtMost(i + chunkSize))
                )
                i += chunkSize
            }
        } else {
            Log.e("Debug ", message)
        }
    }
}

fun printStackTrace(message: Exception) {
    if (BuildConfig.DEBUG) {
        message.printStackTrace()
    }
}

inline fun View.showLongSnack(message: String, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    snack.f()
    snack.show()
}

inline fun View.showShortSnack(message: String, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    snack.f()
    snack.show()
}

fun View.showShortSnack(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}


fun Context.debugToast(msg: String?) {
    if (BuildConfig.DEBUG) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

fun Context.showSuccessToast(msg: String?) {
    msg?.let {
        val toast = Toast(this)
        val inflater = LayoutInflater.from(this)
        val layout: View = inflater.inflate(R.layout.toast_layout_top, null)

        val textV = layout.findViewById<View>(R.id.toast_text) as TextView
        textV.text = msg

        toast.setGravity(Gravity.TOP, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}

fun Context.showFailureToast(msg: String?) {
    msg?.let {
        val toast = Toast(this)
        val inflater = LayoutInflater.from(this)
        val layout: View = inflater.inflate(R.layout.toast_failed_top, null)

        val textV = layout.findViewById<View>(R.id.toast_text) as TextView
        textV.text = msg

        toast.setGravity(Gravity.TOP, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}

fun Context.showDebugToast(msg: String) {
    if (BuildConfig.DEBUG) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

fun Context.showShortToast(msg: String?) {
    msg?.let {
        val toast = Toast(this)
        val inflater = LayoutInflater.from(this)
        val layout: View = inflater.inflate(R.layout.toast_layout_top, null)

        val textV = layout.findViewById<View>(R.id.toast_text) as TextView
        textV.text = msg

        toast.setGravity(Gravity.TOP, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}

fun Context.showLengthToast(msg: String?) {
    msg?.let {
        val toast = Toast(this)
        val inflater = LayoutInflater.from(this)
        val layout: View = inflater.inflate(R.layout.toast_layout_top, null)

        val textV = layout.findViewById<View>(R.id.toast_text) as TextView
        textV.text = msg

        toast.setGravity(Gravity.TOP, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }
}

fun Context.showSnack(message: String) {
    Snackbar.make(
        (this as Activity).findViewById(android.R.id.content),
        message,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun Context.showErrorResponse(errorResponse: String?) {
    if (errorResponse.isNullOrBlank()) {
        showFailureToast("Some error occurred, try again later!")
    } else {
        try {
            if (!TextUtils.isEmpty(errorResponse)) {
                val baseResponse: BaseResponse =
                    GsonBuilder().create().fromJson(errorResponse, BaseResponse::class.java)
                printLog(baseResponse.message)
                showFailureToast(baseResponse.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}