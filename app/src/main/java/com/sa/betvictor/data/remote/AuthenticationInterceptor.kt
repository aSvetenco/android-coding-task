package com.sa.betvictor.data.remote

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthenticationInterceptor(private val token: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(authorizeRequest(chain.request()))
    }

    private fun authorizeRequest(request: Request): Request =
        request.newBuilder()
            .header(
                "Authorization",
                "Bearer $token"
            )
            .build()
}