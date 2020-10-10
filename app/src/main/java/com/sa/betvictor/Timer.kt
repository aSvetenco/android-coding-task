package com.sa.betvictor

import kotlinx.coroutines.*

class Timer {

    private var listener: OnPeriodFinishedListener? = null
    private var timerJob: Job? = null

    fun setOnPeriodFinishedListener(listener: OnPeriodFinishedListener) {
        this.listener = listener
    }

    fun start(period: Long, isPeriodic: Boolean = true, coroutineScope: CoroutineScope) {
        timerJob = coroutineScope.launch {
            withContext(Dispatchers.Default) {
                scheduleTask(period, isPeriodic)
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
    }

    private suspend fun scheduleTask(period: Long, isPeriodic: Boolean = true) {
        delay(period)
        listener?.onPeriodFinished()
        if (isPeriodic) scheduleTask(period, isPeriodic)
    }

    interface OnPeriodFinishedListener {
        fun onPeriodFinished()
    }
}