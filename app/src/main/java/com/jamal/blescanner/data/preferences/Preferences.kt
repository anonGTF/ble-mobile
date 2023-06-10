package com.jamal.blescanner.data.preferences

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.jamal.blescanner.SmartWarehouseApplication
import com.jamal.blescanner.data.preferences.PrefConstants.PREF_KEY
import com.jamal.blescanner.data.preferences.PrefConstants.PREF_LOGGED_IN
import com.jamal.blescanner.data.preferences.PrefConstants.PREF_REFRESH_TOKEN
import com.jamal.blescanner.data.preferences.PrefConstants.PREF_TOKEN
import com.jamal.blescanner.data.preferences.PrefConstants.PREF_USER_ID
import com.jamal.blescanner.data.preferences.PrefConstants.PREF_USER_NAME


class Preferences private constructor() {
    private val mPrefs: SharedPreferences
    private val mEdit: SharedPreferences.Editor

    val token: String?
        get() = instance.mPrefs.getString(PREF_TOKEN, "")

    val refreshToken: String?
        get() = instance.mPrefs.getString(PREF_REFRESH_TOKEN, "")

    val userID: Int
        get() = instance.mPrefs.getInt(PREF_USER_ID, 0)

    val userName: String?
        get() = instance.mPrefs.getString(PREF_USER_NAME, "")

    val loggedIn: Boolean
        get() = instance.mPrefs.getBoolean(PREF_LOGGED_IN, false)

    fun saveToken(value: String?) {
        mEdit.putString(PREF_TOKEN, value)
        mEdit.apply()
    }

    fun saveRefreshToken(value: String?) {
        mEdit.putString(PREF_REFRESH_TOKEN, value)
        mEdit.apply()
    }

    fun saveUserId(value: Int) {
        mEdit.putInt(PREF_USER_ID, value)
        mEdit.apply()
    }

    fun saveUserName(value: String?) {
        mEdit.putString(PREF_USER_NAME, value)
        mEdit.apply()
    }

    fun isLoggedIn(value: Boolean) {
        mEdit.putBoolean(PREF_LOGGED_IN, value)
        mEdit.apply()
    }

    companion object {
        var INSTANCE: Preferences? = null
        val instance: Preferences
            get() {
                if (INSTANCE == null) INSTANCE = Preferences()
                return INSTANCE as Preferences
            }
    }

    init {
        val app: Application = SmartWarehouseApplication.instance
        mPrefs = app.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
        mEdit = mPrefs.edit()
    }
}