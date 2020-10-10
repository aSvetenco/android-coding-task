package com.sa.betvictor.di

import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.sa.betvictor.data.TweetRepository
import com.sa.betvictor.data.remote.ApiService
import com.sa.betvictor.data.remote.AuthenticationInterceptor
import com.sa.betvictor.data.remote.TweetRemoteDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object TweetDependenciesFactory {

    fun viewModelFactory(): ViewModelProvider.Factory {
        return MainViewModelFactory(repository())
    }

    private fun repository() = TweetRepository(tweetRemoteDataSource())

    private fun tweetRemoteDataSource() = TweetRemoteDataSource(api(), gson())

    private fun api() = apiService(gson(), okHttpClient()).twitterStreamApi()

    private fun okHttpClient(): OkHttpClient {
        val inter = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .addInterceptor(AuthenticationInterceptor())
           // .addInterceptor(inter)
            .build()
    }

    private fun gson() = Gson()

    private fun apiService(gson: Gson, okHttpClient: OkHttpClient) =
        ApiService.getInstance(gson, okHttpClient)

}
