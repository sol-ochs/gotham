package com.gotham.app.data.remote.interceptor

import com.gotham.app.data.remote.NycOpenDataApi
import okhttp3.Interceptor
import okhttp3.Response

class AppTokenInterceptor(
    private val appToken: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val request = if (appToken.isNotEmpty()) {
            originalRequest.newBuilder()
                .addHeader(NycOpenDataApi.APP_TOKEN_HEADER, appToken)
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
