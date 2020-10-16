package com.sa.betvictor.app.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sa.betvictor.R
import com.sa.betvictor.common.ActionLiveData
import com.sa.betvictor.common.NetworkStateMonitor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.internal.http2.StreamResetException
import java.net.UnknownHostException

abstract class BaseViewModel(private val networkMonitor: NetworkStateMonitor) : ViewModel() {

    private val viewModelJob = Job()
    protected val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _error = ActionLiveData<Int>()

    abstract val tag: String
    val error: LiveData<Int> = _error

    fun registerNetworkCallback() = networkMonitor.registerNetworkCallback()

    protected fun launchDataLoad(
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
            else -> handleError(throwable)
        }
        Log.e(tag, throwable.message, throwable)
    }

    //Todo - error handling needed - Http, Twitter API errors and edge cases
    private fun handleError(t: Throwable) {
        _error.value = when (t) {
            is StreamResetException -> R.string.error_stream_canceled
            is UnknownHostException -> R.string.error_no_network_connection
            else -> R.string.error_generic
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkMonitor.unregisterNetworkCallback()
        viewModelJob.cancel()
    }
}
