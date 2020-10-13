package com.sa.betvictor.common

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData

class NetworkStateMonitor(private val connectivityManager: ConnectivityManager) {

    private val onNetworkStateChangedListener = ActionLiveData<Boolean>()
    private val networkCallback: NetworkCallback = NetworkCallback(onNetworkStateChangedListener)

    fun registerNetworkCallback(): LiveData<Boolean> {
        register()
        return onNetworkStateChangedListener
    }

    fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun register() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        }
    }

    class NetworkCallback(private val callback: ActionLiveData<Boolean>) : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) = callback.postValue(true)

        override fun onLost(network: Network) = callback.postValue(false)
    }
}