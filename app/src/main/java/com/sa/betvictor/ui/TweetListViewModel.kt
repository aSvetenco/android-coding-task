package com.sa.betvictor.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sa.betvictor.domain.Tweet
import com.sa.betvictor.domain.TweetRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class TweetListViewModel(
    private val repository: TweetRepository,
    private val validator: TweetQueryValidator
) : ViewModel(), TweetQueryValidator.TweetQueryValidationListener {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var fetchTweetsJob: Job? = null

    init {
        validator.listener = this
    }

    var fetchState = MutableLiveData(FetchState.INACTIVE)
    val tweetData = MutableLiveData<List<Tweet>>()
    val progress = MutableLiveData<Boolean>()
    val onInvalidQuery = MutableLiveData<Int>()

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
            fetchTweetsJob = launchDataLoad(doOnError = { progress.value = false }) {
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
        fetchState.value = FetchState.INACTIVE
        Log.e("MainViewModel", throwable.message, throwable)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    enum class FetchState { ACTIVE, INACTIVE }

    override fun onInvalidQuery(errorResId: Int) {
        onInvalidQuery.value = errorResId
    }
}