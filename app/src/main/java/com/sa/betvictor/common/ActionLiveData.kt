package com.sa.betvictor.common

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class ActionLiveData<T> : MutableLiveData<T>() {

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) = super.observe(owner,
            Observer {
                if (it == null) return@Observer
                observer.onChanged(it)
                value = null
            })
}