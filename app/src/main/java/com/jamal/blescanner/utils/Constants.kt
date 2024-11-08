package com.jamal.blescanner.utils

import androidx.annotation.StringRes
import com.jamal.blescanner.R

const val API_KEY = "AIzaSyB2pRTJNGQVvGaSeB0NYhTvzTZOCw5S5KY"
//const val BASE_URL = "https://b836-202-67-40-204.ap.ngrok.io/api/v1/"
const val BASE_URL = "https://ble-backend-production.up.railway.app/api/v1/"
const val NOTIFY = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"
const val UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
const val WRITE = "6e400002-b5a3-f393-e0a9-e50e24dcca9e"
const val PREFIX_NAME = "f305f312"
const val PREFIX_MAJOR = "f303f302"
const val PREFIX_MINOR = "f304f302"
const val PREFIX_PASSWORD = "f300f306"
const val DEFAULT_PASSWORD = "aa14061112"


@StringRes
val TAB_TITLES = intArrayOf(
    R.string.tab_dalam_gudang,
    R.string.tab_lain,
)