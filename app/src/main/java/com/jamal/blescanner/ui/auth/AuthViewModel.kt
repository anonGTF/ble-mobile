package com.jamal.blescanner.ui.auth

import com.jamal.blescanner.base.BaseViewModel
import com.jamal.blescanner.data.preferences.Preferences
import com.jamal.blescanner.data.remote.BaseApi
import com.jamal.blescanner.utils.Utils.getTimeMillis
import com.jamal.blescanner.utils.Utils.orZero
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val pref: Preferences,
    private val api: BaseApi
) : BaseViewModel() {

    fun login(email: String, password: String) = wrapApiWithLiveData(
        apiCall = { api.login(email, password) },
        handleBeforePostSuccess = {
            pref.saveToken(it.content?.tokens?.accessToken?.token.orEmpty())
            pref.saveRefreshToken(it.content?.tokens?.refreshToken?.token.orEmpty())
            pref.saveExpiredMillis(it.content?.tokens?.accessToken?.expirationDateValue.orZero())
            pref.isLoggedIn(true)
            pref.saveUserId(it.content?.user?.id.orZero())
            pref.saveUserName(it.content?.user?.name.orEmpty())
        }
    )

    fun getProfile() = wrapApiWithLiveData(
        apiCall = { api.getProfile(pref.userID) }
    )

    fun logout() = wrapApiWithLiveData(
        apiCall = { api.logout() },
        handleBeforePostSuccess = {
            pref.saveToken("")
            pref.saveRefreshToken("")
            pref.saveExpiredMillis(0L)
            pref.isLoggedIn(false)
            pref.saveUserId(0)
            pref.saveUserName("")
        }
    )

    fun validateLoggedIn() = pref.loggedIn && pref.expiredMillis > getTimeMillis()

}