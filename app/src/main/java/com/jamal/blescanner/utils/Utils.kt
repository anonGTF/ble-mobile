package com.jamal.blescanner.utils

import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun Boolean.toGender(): String = if (this) "Laki-laki" else "Perempuan"

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun String.safeParseDouble(): Double = if (this.isEmpty()) 0.0 else this.toDouble()

    fun Double?.orZero() = this ?: 0.0

    fun Int?.orZero() = this ?: 0

    fun Int?.orOne() = this ?: 1

    fun Boolean?.orFalse() = this ?: false

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun getTimeMillis() = System.currentTimeMillis().toString()

    fun ByteArray.toUuid(): String {
        val hexChars = "0123456789ABCDEF"
        val hexArray = CharArray(this.size * 2)

        for (i in this.indices) {
            val value = this[i].toInt() and 0xFF
            hexArray[i * 2] = hexChars[value ushr 4]
            hexArray[i * 2 + 1] = hexChars[value and 0x0F]
        }

        return String(hexArray)
    }

    fun <T> MutableList<T>.getOrDefault(index: Int, default: T): T {
        return try {
            get(index)
        } catch (e: IndexOutOfBoundsException) {
            default
        }
    }

}