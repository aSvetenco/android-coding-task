package com.sa.betvictor.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sa.betvictor.data.TweetRepository
import com.sa.betvictor.domain.Tweet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val repository: TweetRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val tweetData = MutableLiveData<List<Tweet>>()

    fun getTweets() {
        launchDataLoad {
            repository.getTweets().collect {
                tweetData.value = it
            }
        }
    }

    fun fetchStatuses() {
        launchDataLoad {
            repository.fetchTweets("Hello World")
        }
    }

    private fun launchDataLoad(
        doOnError: (Throwable) -> Unit = { onError(it) },
        doOnComplete: () -> Unit = { },
        block: suspend () -> Unit
    ): Job = uiScope.launch {
        try {
            block()
        } catch (t: Throwable) {
            doOnError(t)
        } finally {
            doOnComplete()
        }
    }

    private fun onError(throwable: Throwable) {
        Log.e("MainViewModel", throwable.message, throwable)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}