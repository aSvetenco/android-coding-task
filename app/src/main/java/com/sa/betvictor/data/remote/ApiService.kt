package com.sa.betvictor.data.remote

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiService private constructor(
    private val gson: Gson,
    private val okHttpClient: OkHttpClient
) {

    var retrofit: Retrofit? = null

    fun twitterStreamApi(): Api = retrofit().create(Api::class.java)

    private fun retrofit(): Retrofit {
        return retrofit ?: Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    companion object {
        private const val HOST = "api.twitter.com"
        private const val SCHEMA = "https://"
        private const val BASE_URL = "$SCHEMA$HOST/"

        @Volatile
        private var instance: ApiService? = null

        fun getInstance(
            gson: Gson,
            okHttpClient: OkHttpClient,
        ): ApiService =
            instance ?: synchronized(this) { ApiService(gson, okHttpClient).also { instance = it } }
    }
}