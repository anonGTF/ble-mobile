package com.jamal.blescanner.utils

import android.util.Log

object LogUtil {
    const val DEBUG = false
    const val ERROR = true
    const val INFO = false
    const val ZBKCTAG = "Beacon"
    fun d(str: String?) {}
    fun d(str: String?, str2: String?) {}
    fun d(str: String?, str2: String?, th: Throwable?) {}
    fun d(str: String?, th: Throwable?) {}
    fun i(str: String?) {}
    fun i(str: String?, str2: String?) {}
    fun i(str: String?, str2: String?, th: Throwable?) {}
    fun i(str: String?, th: Throwable?) {}
    fun e(str: String?) {
        e(ZBKCTAG, str)
    }

    fun e(str: String?, th: Throwable?) {
        e(ZBKCTAG, str, th)
    }

    fun e(str: String?, str2: String?, th: Throwable?) {
        Log.e(str, str2, th)
    }

    fun e(str: String?, str2: String?) {
        Log.e(str, str2, null)
    }
}