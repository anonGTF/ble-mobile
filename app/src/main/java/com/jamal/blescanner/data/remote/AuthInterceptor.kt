package com.jamal.blescanner.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val token: String?, private val refresh: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val basicRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("x-refresh-token", refresh.orEmpty())
            .build()
        return chain.proceed(basicRequest)
    }
}