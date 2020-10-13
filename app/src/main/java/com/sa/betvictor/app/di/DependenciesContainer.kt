package com.sa.betvictor.app.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.sa.betvictor.BuildConfig
import com.sa.betvictor.common.NetworkStateMonitor
import com.sa.betvictor.common.Timer
import com.sa.betvictor.data.local.TweetEntityMapper
import com.sa.betvictor.data.local.TweetLocalDataSource
import com.sa.betvictor.data.local.database.TweetsDatabase
import com.sa.betvictor.data.remote.ApiService
import com.sa.betvictor.data.remote.AuthenticationInterceptor
import com.sa.betvictor.data.remote.TweetDtoMapper
import com.sa.betvictor.data.remote.TweetRemoteDataSource
import com.sa.betvictor.domain.TweetRepository
import com.sa.betvictor.ui.TweetQueryValidator
import okhttp3.OkHttpClient

class DependenciesContainer(private val app: Application) {

    private val gson = Gson()

    private var connectivityManager: ConnectivityManager? = null

    fun tweetVMFactory(): ViewModelProvider.Factory =
            TweetViewModelFactory(repository(), tweetQueryValidator(), networkMonitor(), timer())

    private fun repository() = TweetRepository(tweetRemoteDataSource(), tweetLocalDataSource())

    private fun tweetRemoteDataSource() = TweetRemoteDataSource(api(), gson, tweetDtoMapper())

    private fun tweetLocalDataSource() =
            TweetLocalDataSource(tweetDao(), ruleDao(), tweetEntityMapper())

    private fun api() = apiService(okHttpClient()).twitterStreamApi()

    private fun okHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(AuthenticationInterceptor(BuildConfig.TWITTER_TOKEN))
                .build()
    }

    private fun apiService(okHttpClient: OkHttpClient) = ApiService.getInstance(gson, okHttpClient)

    private fun tweetsDatabase() = TweetsDatabase.getInstance(app.applicationContext)

    private fun tweetDao() = tweetsDatabase().tweetDao()

    private fun ruleDao() = tweetsDatabase().ruleDao()

    private fun tweetDtoMapper() = TweetDtoMapper()

    private fun tweetEntityMapper() = TweetEntityMapper()

    private fun tweetQueryValidator() = TweetQueryValidator()

    private fun networkMonitor() = NetworkStateMonitor(connectivityManager())

    private fun connectivityManager(): ConnectivityManager {
        return connectivityManager ?: (app.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as
                ConnectivityManager).also { connectivityManager = it }
    }

    private fun timer() = Timer()

}

