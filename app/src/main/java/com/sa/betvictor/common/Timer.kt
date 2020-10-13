package com.sa.betvictor.common

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Timer {

    private var listener: OnScheduledTimerExpiredListener? = null
    private var timerJob: Job? = null

    fun setOnPeriodFinishedListener(listener: OnScheduledTimerExpiredListener) {
        this.listener = listener
    }

    fun schedule(coroutineScope: CoroutineScope, period: Long, isRepeating: Boolean = true) {
        if (timerJob?.isActive == true) stop()
        timerJob = coroutineScope.launch {
            withContext(Dispatchers.Default) {
                scheduleTask(period, isRepeating)
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
    }

    private suspend fun scheduleTask(period: Long, isRepeating: Boolean) {
        delay(period)
        listener?.onScheduledTimerExpired()
        if (isRepeating) scheduleTask(period, isRepeating)
    }

    interface OnScheduledTimerExpiredListener {
        suspend fun onScheduledTimerExpired()
    }
}