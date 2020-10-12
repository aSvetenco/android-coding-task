package com.sa.betvictor.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sa.betvictor.R
import com.sa.betvictor.app.App
import com.sa.betvictor.common.hideKeyboard
import com.sa.betvictor.common.toast
import com.sa.betvictor.domain.Tweet
import com.sa.betvictor.ui.TweetListViewModel.FetchState.ACTIVE
import com.sa.betvictor.ui.TweetListViewModel.FetchState.INACTIVE
import kotlinx.android.synthetic.main.fragment_tweet_list.*

class TweetListFragment : Fragment(R.layout.fragment_tweet_list) {

    private val adapter = TweetAdapter()
    private var onActionClick: () -> Unit = {}

    private val viewModel: TweetListViewModel by viewModels {
        (requireActivity().application as App).container.viewModelFactory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.tweetData.observe(viewLifecycleOwner, ::onTweetsLoaded)
        viewModel.progress.observe(viewLifecycleOwner, ::showLoading)
        viewModel.fetchState.observe(viewLifecycleOwner, ::handleFetchState)
        viewModel.onInvalidQuery.observe(viewLifecycleOwner, ::onInvalidQuery)
        viewModel.onNetworkUnavailable.observe(viewLifecycleOwner, ::showToast)
        viewModel.getTweets()

        actionBtn.setOnClickListener {
            requireContext().hideKeyboard(view)
            onActionClick()
        }
        tweetList.adapter = adapter
        tweetList.setHasFixedSize(true)
        searchField.doOnTextChanged { _, _, _, _ -> searchField.error = null }
    }

    private fun onTweetsLoaded(list: List<Tweet>) {
        adapter.submitList(list) { tweetList.scrollToPosition(list.size - 1) }
    }

    private fun showLoading(isLoading: Boolean) {
        progress.isVisible = isLoading
    }

    private fun handleFetchState(state: TweetListViewModel.FetchState) {
        when (state) {
            INACTIVE -> {
                actionBtn.text = getString(R.string.action_btn_start)
                onActionClick = { viewModel.fetchStatuses(searchField.text.toString()) }
            }
            ACTIVE -> {
                actionBtn.text = getString(R.string.action_btn_stop)
                onActionClick = { viewModel.cancelFetchTweets() }
            }
        }
    }

    private fun onInvalidQuery(@StringRes errorRes: Int) {
        searchField.error = getString(errorRes)
    }

    private fun showToast(@StringRes messageRes: Int) {
        requireContext().toast(messageRes)
    }

    companion object {
        fun newInstance() = TweetListFragment()
    }
}