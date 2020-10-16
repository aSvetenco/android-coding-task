package com.sa.betvictor.app.di

import com.google.gson.Gson
import com.sa.betvictor.BuildConfig
import com.sa.betvictor.data.remote.Api
import com.sa.betvictor.data.remote.ApiService
import com.sa.betvictor.data.remote.AuthenticationInterceptor
import okhttp3.OkHttpClient

class NetworkModule(private val gson: Gson) {

    private val okHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthenticationInterceptor(BuildConfig.TWITTER_TOKEN))
            .build()

    fun api(): Api = apiService().twitterStreamApi()

    private fun apiService() = ApiService.getInstance(gson, okHttpClient)

}
