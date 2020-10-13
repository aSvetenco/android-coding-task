package com.sa.betvictor.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sa.betvictor.R
import com.sa.betvictor.common.ActionLiveData
import com.sa.betvictor.common.NetworkStateMonitor
import com.sa.betvictor.common.Timer
import com.sa.betvictor.common.Timer.OnScheduledTimerExpiredListener
import com.sa.betvictor.domain.Tweet
import com.sa.betvictor.domain.TweetRepository
import com.sa.betvictor.ui.TweetListViewModel.FetchState.ACTIVE
import com.sa.betvictor.ui.TweetListViewModel.FetchState.INACTIVE
import com.sa.betvictor.ui.TweetQueryValidator.TweetQueryValidationListener
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.internal.http2.StreamResetException
import java.net.UnknownHostException

class TweetListViewModel(
    private val repository: TweetRepository,
    private val validator: TweetQueryValidator,
    private val networkMonitor: NetworkStateMonitor,
    private val timer: Timer
) : ViewModel(), TweetQueryValidationListener, OnScheduledTimerExpiredListener {

    private val TAG = TweetListViewModel::class.java.simpleName
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var fetchTweetsJob: Job? = null

    private val _fetchState = MutableLiveData(INACTIVE)
    private val _tweetData = MutableLiveData<List<Tweet>>()
    private val _progress = MutableLiveData<Boolean>()
    private val _onInvalidQuery = MutableLiveData<Int>()
    private val _error = ActionLiveData<Int>()

    val fetchState: LiveData<FetchState> = _fetchState
    val tweetData: LiveData<List<Tweet>> = _tweetData
    val progress: LiveData<Boolean> = _progress
    val onInvalidQuery: LiveData<Int> = _onInvalidQuery
    val error: LiveData<Int> = _error

    init {
        validator.listener = this
        timer.setOnPeriodFinishedListener(this)
    }

    fun registerNetworkCallback() = networkMonitor.registerNetworkCallback()

    fun getTweets() {
        launchDataLoad {
            repository.getTweets().collect {
                _progress.value = false
                _tweetData.value = it
            }
        }
    }

    fun fetchTweets(query: String) {
        if (validator.isValid(query)) {
            _progress.value = true
            _fetchState.value = ACTIVE
            scheduleTimer()
            fetchTweetsJob = launchDataLoad(doOnError = ::onFetchTweetsFails) {
                repository.fetchTweets(query)
            }
        }
    }

    fun cancelFetchTweets() {
        if (fetchTweetsJob?.isActive == true) {
            fetchTweetsJob?.cancel()
            repository.cancelStreamedCall()
        }
    }

    private fun onFetchTweetsFails(t: Throwable) {
        _progress.value = false
        _fetchState.value = INACTIVE
        timer.stop()
    }

    private fun scheduleTimer() {
        val period = if (TWEET_LIFESPAN > ONE_SECOND) ONE_SECOND else TWEET_LIFESPAN
        timer.schedule(uiScope, period)
    }

    override fun onInvalidQuery(errorResId: Int) {
        _onInvalidQuery.value = errorResId
    }

    override suspend fun onScheduledTimerExpired() {
        val condition = System.currentTimeMillis() - TWEET_LIFESPAN
        val deletedTweets = repository.clearExpiredTweets(condition)
        if (deletedTweets == 0 && fetchTweetsJob?.isActive != true) timer.stop()
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

    private fun onError(throwable: Throwable) {
        when (throwable) {
            is CancellationException -> Unit

            //Todo - error handling needed - Http, Twitter API errors and edge cases
            is StreamResetException -> _error.value = R.string.error_stream_canceled
            is UnknownHostException -> _error.value = R.string.error_no_network_connection
            else -> _error.value = R.string.error_generic
        }
        Log.e(TAG, throwable.message, throwable)
    }

    override fun onCleared() {
        super.onCleared()
        networkMonitor.unregisterNetworkCallback()
        viewModelJob.cancel()
    }

    fun onNetworkAvailable() {
        scheduleTimer()
    }

    enum class FetchState { ACTIVE, INACTIVE }

    private companion object {
        const val TWEET_LIFESPAN = 10_000L
        const val ONE_SECOND = 1_000L
    }
}