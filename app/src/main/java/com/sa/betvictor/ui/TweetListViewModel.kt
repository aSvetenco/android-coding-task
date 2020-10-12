package com.sa.betvictor.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sa.betvictor.R
import com.sa.betvictor.common.NetworkStateMonitor
import com.sa.betvictor.common.Timer
import com.sa.betvictor.domain.Tweet
import com.sa.betvictor.domain.TweetRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class TweetListViewModel(
    private val repository: TweetRepository,
    private val validator: TweetQueryValidator,
    private val networkMonitor: NetworkStateMonitor,
    private val timer: Timer
) : ViewModel(), TweetQueryValidator.TweetQueryValidationListener,
    NetworkStateMonitor.OnNetworkAvailableListener, Timer.OnScheduledTimerExpiredListener {

    private val TAG = TweetListViewModel::class.java.simpleName
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var fetchTweetsJob: Job? = null

    init {
        validator.listener = this
        timer.setOnPeriodFinishedListener(this)
        networkMonitor.addNetworkCallback(this)
    }

    var fetchState = MutableLiveData(FetchState.INACTIVE)
    val tweetData = MutableLiveData<List<Tweet>>()
    val progress = MutableLiveData<Boolean>()
    val onInvalidQuery = MutableLiveData<Int>()
    val onNetworkUnavailable = MutableLiveData<Int>()

    fun getTweets() {
        launchDataLoad {
            repository.getTweets().collect {
                progress.value = false
                tweetData.value = it
            }
        }
    }

    fun fetchStatuses(query: String) {
        if (validator.isValid(query)) {
            progress.value = true
            fetchState.value = FetchState.ACTIVE
            scheduleTimer()
            fetchTweetsJob = launchDataLoad(doOnError = ::onFetchTweetsFails) {
                repository.fetchTweets(query)
            }
        }
    }

    fun cancelFetchTweets() {
        if (fetchTweetsJob?.isActive == true) {
            fetchTweetsJob?.cancelChildren()
            repository.cancelStreamedCall()
        }
    }

    private fun onFetchTweetsFails(t: Throwable) {
        progress.value = false
        fetchState.value = FetchState.INACTIVE
        timer.stop()
    }

    private fun onError(throwable: Throwable) {
        when (throwable) {
            is CancellationException -> Unit
        }
        Log.e(TAG, throwable.message, throwable)
    }

    private fun scheduleTimer() {
        val period = if (TWEET_LIFESPAN > ONE_SECOND) ONE_SECOND else TWEET_LIFESPAN
        timer.schedule(uiScope, period)
    }

    override fun onCleared() {
        super.onCleared()
        networkMonitor.unregister()
        viewModelJob.cancel()
    }

    override fun onInvalidQuery(errorResId: Int) {
        onInvalidQuery.value = errorResId
    }

    override fun onNetworkIsAvailable(isNetworkAvailable: Boolean) {
        if (isNetworkAvailable) scheduleTimer()
        else {
            cancelFetchTweets()
            timer.stop()
            onNetworkUnavailable.postValue(R.string.error_no_network_connection)
        }
    }

    override suspend fun onScheduledTimerExpired() {
        val condition = System.currentTimeMillis() - TWEET_LIFESPAN
        repository.clearExpiredTweets(condition)
    }

    private fun launchDataLoad(
        doOnError: (Throwable) -> Unit = { },
        doOnComplete: () -> Unit = { },
        block: suspend () -> Unit
    ): Job = uiScope.launch {
        try {
            block()
        } catch (t: Throwable) {
            doOnError(t)
            onError(t)
        } finally {
            doOnComplete()
        }
    }

    enum class FetchState { ACTIVE, INACTIVE }

    private companion object {
        const val TWEET_LIFESPAN = 10_000L
        const val ONE_SECOND = 1_000L
    }
}