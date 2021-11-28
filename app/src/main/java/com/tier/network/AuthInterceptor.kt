package com.tier.network


import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(val secretKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val newRequest = originalRequest.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("secret-key", secretKey)
            .build();

        return chain.proceed(newRequest)
    }
}
