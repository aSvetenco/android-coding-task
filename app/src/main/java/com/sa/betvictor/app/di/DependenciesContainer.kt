package com.sa.betvictor.app.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.sa.betvictor.common.NetworkStateMonitor
import com.sa.betvictor.common.Timer
import com.sa.betvictor.data.local.TweetEntityMapper
import com.sa.betvictor.data.local.TweetLocalDataSource
import com.sa.betvictor.data.remote.TweetDtoMapper
import com.sa.betvictor.data.remote.TweetRemoteDataSource
import com.sa.betvictor.domain.TweetRepository
import com.sa.betvictor.ui.TweetQueryValidator

class DependenciesContainer(private val app: Application) {

    private val gson = Gson()

    private val networkModule = NetworkModule(gson)

    private val databaseModule = DatabaseModule(app)

    private var connectivityManager: ConnectivityManager? = null

    fun tweetVMFactory(): ViewModelProvider.Factory =
        TweetViewModelFactory(repository(), tweetQueryValidator(), networkMonitor(), timer())

    private fun repository() = TweetRepository(tweetRemoteDataSource(), tweetLocalDataSource())

    private fun tweetRemoteDataSource() =
        TweetRemoteDataSource(networkModule.api(), gson, tweetDtoMapper())

    private fun tweetLocalDataSource() =
        TweetLocalDataSource(
            databaseModule.tweetDao(),
            databaseModule.ruleDao(),
            tweetEntityMapper()
        )

    private fun tweetDtoMapper() = TweetDtoMapper()

    private fun tweetEntityMapper() = TweetEntityMapper()

    private fun tweetQueryValidator() = TweetQueryValidator()

    private fun networkMonitor() = NetworkStateMonitor(connectivityManager())

    private fun connectivityManager(): ConnectivityManager {
        return connectivityManager
            ?: (app.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as
                    ConnectivityManager).also { connectivityManager = it }
    }

    private fun timer() = Timer()

}

