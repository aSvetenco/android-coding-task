package com.sa.betvictor.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sa.betvictor.data.TweetRepository
import com.sa.betvictor.ui.MainViewModel

class MainViewModelFactory(private val repository: TweetRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass != MainViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        return MainViewModel(repository) as T
    }
}