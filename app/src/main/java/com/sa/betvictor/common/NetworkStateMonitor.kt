package com.sa.betvictor.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build

class NetworkStateMonitor(context: Context) {

    private val networkCallback: NetworkCallback = NetworkCallback()
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun addNetworkCallback(callback: OnNetworkAvailableListener) {
        networkCallback.onNetworkStateChangedListener = callback
        register()
    }

    fun unregister() {
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

    class NetworkCallback : ConnectivityManager.NetworkCallback() {

        var onNetworkStateChangedListener: OnNetworkAvailableListener? = null

        override fun onAvailable(network: Network) {
            onNetworkStateChangedListener?.onNetworkIsAvailable(true)
        }

        override fun onLost(network: Network) {
            onNetworkStateChangedListener?.onNetworkIsAvailable(false)
        }
    }

    interface OnNetworkAvailableListener {
        fun onNetworkIsAvailable(isNetworkAvailable: Boolean)
    }
}