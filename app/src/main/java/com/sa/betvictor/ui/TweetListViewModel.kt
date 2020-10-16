package com.sa.betvictor.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sa.betvictor.app.base.BaseViewModel
import com.sa.betvictor.common.NetworkStateMonitor
import com.sa.betvictor.common.Timer
import com.sa.betvictor.common.Timer.OnScheduledTimerExpiredListener
import com.sa.betvictor.domain.Tweet
import com.sa.betvictor.domain.TweetRepository
import com.sa.betvictor.ui.TweetListViewModel.FetchState.ACTIVE
import com.sa.betvictor.ui.TweetListViewModel.FetchState.INACTIVE
import com.sa.betvictor.ui.TweetQueryValidator.TweetQueryValidationListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect

class TweetListViewModel(
    private val repository: TweetRepository,
    private val validator: TweetQueryValidator,
    private val timer: Timer,
    networkMonitor: NetworkStateMonitor
) : BaseViewModel(networkMonitor), TweetQueryValidationListener, OnScheduledTimerExpiredListener {

    override val tag: String = TweetListViewModel::class.java.simpleName
    private var fetchTweetsJob: Job? = null

    private val _fetchState = MutableLiveData(INACTIVE)
    private val _tweetData = MutableLiveData<List<Tweet>>()
    private val _onInvalidQuery = MutableLiveData<Int>()
    private val _progress = MutableLiveData<Boolean>()

    val progress: LiveData<Boolean> = _progress
    val fetchState: LiveData<FetchState> = _fetchState
    val tweetData: LiveData<List<Tweet>> = _tweetData
    val onInvalidQuery: LiveData<Int> = _onInvalidQuery

    init {
        validator.listener = this
        timer.setOnPeriodFinishedListener(this)
    }

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

    fun onNetworkAvailable() {
        scheduleTimer()
    }

    override fun onInvalidQuery(errorResId: Int) {
        _onInvalidQuery.value = errorResId
    }

    override suspend fun onScheduledTimerExpired() {
        val condition = System.currentTimeMillis() - TWEET_LIFESPAN
        val deletedTweets = repository.clearExpiredTweets(condition)
        if (deletedTweets == 0 && fetchTweetsJob?.isActive != true) timer.stop()
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

    enum class FetchState { ACTIVE, INACTIVE }

    private companion object {
        const val TWEET_LIFESPAN = 10_000L
        const val ONE_SECOND = 1_000L
    }
}
