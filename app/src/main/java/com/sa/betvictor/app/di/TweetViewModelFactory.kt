package com.sa.betvictor.app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sa.betvictor.common.NetworkStateMonitor
import com.sa.betvictor.common.Timer
import com.sa.betvictor.domain.TweetRepository
import com.sa.betvictor.ui.TweetListViewModel
import com.sa.betvictor.ui.TweetQueryValidator

class TweetViewModelFactory(
    private val repository: TweetRepository,
    private val validator: TweetQueryValidator,
    private val networkMonitor: NetworkStateMonitor,
    private val timer: Timer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass != TweetListViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        return TweetListViewModel(repository, validator, networkMonitor, timer) as T
    }
}