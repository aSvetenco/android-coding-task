package com.sa.betvictor.domain

import com.sa.betvictor.data.local.TweetLocalClient
import com.sa.betvictor.data.remote.TweetRemoteClient
import com.sa.betvictor.data.remote.TweetRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TweetRepository(
    private val remoteDataSource: TweetRemoteClient,
    private val localDataSource: TweetLocalClient
) : TweetRemoteDataSource.OnTweetsLoadedListener {

    fun getTweets() = localDataSource.getTweets()

    suspend fun clearExpiredTweets(conditionTime: Long) {
        localDataSource.clearExpiredTweets(conditionTime)
    }

    suspend fun fetchTweets(query: String) =
        withContext(Dispatchers.IO) {
            clearRules()
            addRule(query)
            remoteDataSource.getTweets(this@TweetRepository)
        }

    fun cancelStreamedCall() = remoteDataSource.cancelStreamedCall()

    private suspend fun clearRules() {
        val rules = localDataSource.getRuleIds()
        if (rules.isNotEmpty()) {
            remoteDataSource.deleteRule(rules)
            localDataSource.clearRules()
        }
    }

    private suspend fun addRule(query: String) {
        val ruleIds = remoteDataSource.addRule(query)
        localDataSource.saveRules(ruleIds)
    }

    override suspend fun onTweetsLoaded(tweets: List<Tweet>) {
        localDataSource.saveTweets(tweets)
    }
}