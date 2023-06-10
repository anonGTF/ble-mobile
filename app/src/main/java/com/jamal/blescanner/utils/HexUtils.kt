package com.jamal.blescanner.utils

import com.jamal.blescanner.utils.LogUtil.e
import java.io.UnsupportedEncodingException
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.pow


object HexUtils {
    private const val A_Value = 50.0
    private const val n_Value = 2.5
    fun backchar(i: Int): Char {
        return i.toChar()
    }

    fun hexStr2Str(str: String): String {
        val charArray = str.toCharArray()
        val length = str.length / 2
        val bArr = ByteArray(length)
        for (i in 0 until length) {
            val i2 = i * 2
            bArr[i] = ("0123456789ABCDEF".indexOf(charArray[i2]) * 16 + "0123456789ABCDEF".indexOf(
                charArray[i2 + 1]
            ) and 255).toByte()
        }
        return String(bArr)
    }

    fun bytes2HexString(bArr: ByteArray): String {
        var str = ""
        for (b in bArr) {
            var hexString = Integer.toHexString(b.toInt() and 255)
            if (hexString.length == 1) {
                hexString = "0$hexString"
            }
            str += hexString.uppercase(Locale.getDefault())
        }
        return str
    }

    fun byte2HexString(bArr: ByteArray?): String {
        var str = ""
        if (bArr != null) {
            for (b in bArr) {
                val valueOf = java.lang.Byte.valueOf(b)
                str += String.format("%02X", Integer.valueOf(valueOf.toInt() and 255))
            }
        }
        return str
    }

    fun toBytes(str: String?): ByteArray {
        if (str.isNullOrEmpty() || str.trim { it <= ' ' } == "") {
            return ByteArray(0)
        }
        val bArr = ByteArray(str.length / 2)
        for (i in 0 until str.length / 2) {
            val i2 = i * 2
            bArr[i] = str.substring(i2, i2 + 2).toInt(16).toByte()
        }
        return bArr
    }

    fun printHexString(bArr: ByteArray): String {
        var str = ""
        for (b in bArr) {
            var hexString = Integer.toHexString(b.toInt() and 255)
            if (hexString.length == 1) {
                hexString = "0$hexString"
            }
            str += hexString
        }
        return str
    }

    fun bytesToHexString(bArr: ByteArray?): String? {
        val sb = StringBuilder("")
        if (bArr == null || bArr.isEmpty()) {
            return null
        }
        for (b in bArr) {
            val hexString = Integer.toHexString(b.toInt() and 255)
            if (hexString.length < 2) {
                sb.append(0)
            }
            sb.append(hexString)
        }
        return sb.toString()
    }

    fun HexToInt(str: String): Int {
        if (IsHex(str)) {
            var upperCase = str.uppercase(Locale.getDefault())
            if (upperCase.length > 2 && upperCase[0] == '0' && upperCase[1] == 'X') {
                upperCase = upperCase.substring(2)
            }
            val length = upperCase.length
            var i = 0
            for (i2 in 0 until length) {
                try {
                    i += GetHex(upperCase[length - i2 - 1]) * GetPower(16, i2)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return i
        }
        return 0
    }

    fun IsHex(str: String): Boolean {
        var i = 2
        if (str.length <= 2 || str[0] != '0' || str[1] != 'X' && str[1] != 'x') {
            i = 0
        }
        while (i < str.length) {
            val charAt = str[i]
            if ((charAt < '0' || charAt > '9') && (charAt < 'A' || charAt > 'F') && (charAt < 'a' || charAt > 'f')) {
                return false
            }
            i++
        }
        return true
    }

    @Throws(Exception::class)
    fun GetHex(c: Char): Int {
        if (c < '0' || c > '9') {
            var c2 = 'a'
            if (c < 'a' || c > 'f') {
                c2 = 'A'
                if (c < 'A' || c > 'F') {
                    throw Exception("error param")
                }
            }
            return c.code - c2.code + 10
        }
        return c.code - '0'.code
    }

    @Throws(Exception::class)
    fun GetPower(i: Int, i2: Int): Int {
        if (i2 >= 0) {
            var i3 = 1
            if (i2 == 0) {
                return 1
            }
            for (i4 in 0 until i2) {
                i3 *= i
            }
            return i3
        }
        throw Exception("nCount can't small than 1!")
    }

    fun IntToHex(str: String): String {
        val hexString = Integer.toHexString(str.toInt())
        return if (hexString.length == 1) {
            "0$hexString"
        } else hexString
    }

    fun randomHexString(i: Int): String {
        val stringBuffer = StringBuffer()
        for (i2 in 0 until i) {
            stringBuffer.append("0")
        }
        return stringBuffer.toString().uppercase(Locale.getDefault())
    }

    fun timeToDate(str: String?): String {
        if (str.isNullOrEmpty() || str == "null") {
            return ""
        }
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return simpleDateFormat.format(Date(Long.valueOf(str + "000").toLong()))
    }

    fun toUTF8(str: String): String {
        try {
            if (str == String(str.toByteArray(charset("GB2312")), charset("GB2312"))) {
                return String(str.toByteArray(charset("GB2312")), Charsets.UTF_8)
            }
        } catch (_: Exception) {}
        try {
            if (str == String(str.toByteArray(charset("ISO-8859-1")), charset("ISO-8859-1"))) {
                return String(str.toByteArray(charset("ISO-8859-1")), Charsets.UTF_8)
            }
        } catch (_: Exception) {}
        try {
            if (str == String(str.toByteArray(charset("GBK")), charset("GBK"))) {
                return String(str.toByteArray(charset("GBK")), Charsets.UTF_8)
            }
        } catch (_: Exception) {}
        return str
    }

    fun strToUnicode(str: String): String? {
        return try {
            val stringBuffer = StringBuffer()
            val bytes = str.toByteArray(charset("unicode"))
            var i = 0
            while (i < bytes.size - 1) {
                val hexString = Integer.toHexString(bytes[i + 1].toInt() and 255)
                for (length in hexString.length..1) {
                    stringBuffer.append("0")
                }
                val hexString2 = Integer.toHexString(bytes[i].toInt() and 255)
                stringBuffer.append(hexString)
                stringBuffer.append(hexString2)
                i += 2
            }
            stringBuffer.toString()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            null
        }
    }

    fun unicodeToStr(str: String): String {
        var str1 = str
        val matcher: Matcher = Pattern.compile("(\\\\u(\\p{XDigit}{4}))").matcher(str1)
        while (matcher.find()) {
            val group = matcher.group(1)
            str1 = str1.replace(group, matcher.group(2).toInt(16).toChar().toString() + "")
        }
        return str
    }

    fun SumStrAscii(str: String): Int {
        var i = 0
        for (b in str.toByteArray()) {
            i += b.toInt()
        }
        return i
    }

    fun getAsc(str: String): Int {
        return str.toByteArray()[0].toInt()
    }

    fun hex2Str(str: String): String {
        var i = 0
        var i2 = 0
        val sb = StringBuilder()
        val length = str.length / 2
        for (i3 in 0 until length) {
            val i4 = length - 1
            if (i3 == i4) {
                val parseInt = str.substring(i4 * 2).toInt(16)
                e("bbb:" + str.substring(i2))
                sb.append(parseInt.toChar())
            } else {
                val i5 = i3 * 2
                val parseInt2 = str.substring(i5, i5 + 2).toInt(16)
                e("aaaa:" + str.substring(i5, i))
                sb.append(parseInt2.toChar())
            }
        }
        return sb.toString()
    }

    fun str2Hex(str: String): String {
        val sb = StringBuilder()
        for (element in str) {
            sb.append(Integer.toHexString(element.code))
        }
        return sb.toString()
    }

    fun MacFormat(str: String): String {
        val stringBuffer = StringBuffer()
        stringBuffer.append(str.substring(0, 2).uppercase(Locale.getDefault()))
        stringBuffer.append(":")
        stringBuffer.append(str.substring(2, 4).uppercase(Locale.getDefault()))
        stringBuffer.append(":")
        stringBuffer.append(str.substring(4, 6).uppercase(Locale.getDefault()))
        stringBuffer.append(":")
        stringBuffer.append(str.substring(6, 8).uppercase(Locale.getDefault()))
        stringBuffer.append(":")
        stringBuffer.append(str.substring(8, 10).uppercase(Locale.getDefault()))
        stringBuffer.append(":")
        stringBuffer.append(str.substring(10, 12).uppercase(Locale.getDefault()))
        return stringBuffer.toString()
    }

    fun addZeroForNum(str: String, i: Int, isPrefix: Boolean = false): String {
        var str1 = str
        var length = str1.length
        while (length < i) {
            val stringBuffer = StringBuffer()
            if (isPrefix) {
                stringBuffer.append("0")
                stringBuffer.append(str1)
            } else {
                stringBuffer.append(str1)
                stringBuffer.append("0")
            }
            str1 = stringBuffer.toString()
            length = str1.length
        }
        return str1
    }

    fun addZeroForNum2(str: String, i: Int): String {
        var str1 = str
        var length = str1.length
        while (length < i) {
            val stringBuffer = StringBuffer()
            stringBuffer.append("0")
            stringBuffer.append(str1)
            str1 = stringBuffer.toString()
            length = str1.length
        }
        return str1
    }

    fun getDistance(i: Int): Double {
        val abs = abs(i).toDouble()
        java.lang.Double.isNaN(abs)
        return 10.0.pow((abs - A_Value) / 25.0)
    }
}