package com.sa.betvictor.common

import kotlinx.coroutines.*

class Timer {

    private var listener: OnPeriodFinishedListener? = null
    private var timerJob: Job? = null

    fun setOnPeriodFinishedListener(listener: OnPeriodFinishedListener) {
        this.listener = listener
    }

    fun schedule(coroutineScope: CoroutineScope, period: Long, isRepeating: Boolean = true) {
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
        listener?.onPeriodFinished()
        if (isRepeating) scheduleTask(period, isRepeating)
    }

    interface OnPeriodFinishedListener {
        fun onPeriodFinished()
    }
}