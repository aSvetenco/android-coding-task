package com.sa.betvictor.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sa.betvictor.R
import com.sa.betvictor.di.TweetDependenciesFactory.viewModelFactory
import com.sa.betvictor.domain.Tweet
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val adapter = TweetAdapter()
    private val viewModel: MainViewModel by viewModels { viewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.tweetData.observe(this, ::onTweetsLoaded)
        search.setOnClickListener { viewModel.fetchStatuses() }
        adapter.submitList(listOf(
            Tweet("1111", "1111"),
            Tweet("2222", "2222"),
            Tweet("3333", "3333"),
            Tweet("4444", "4444")
        ))
        tweetList.adapter = adapter
        tweetList.setHasFixedSize(true)
    }

    private fun onTweetsLoaded(list: List<Tweet>) {
        adapter.submitList(list) { tweetList.scrollToPosition(list.size - 1) }
    }
}