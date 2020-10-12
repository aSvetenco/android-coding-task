package com.sa.betvictor.domain

import com.sa.betvictor.data.local.TweetLocalClient
import com.sa.betvictor.data.remote.TweetRemoteClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

class TweetRepository(
        private val remoteDataSource: TweetRemoteClient,
        private val localDataSource: TweetLocalClient) {

    fun getTweets() = localDataSource.getTweets()

    suspend fun fetchTweets(query: String) {
        clearRules()
        addRule(query)
        remoteDataSource.fetchTweets().collect {
            localDataSource.saveTweets(it)
        }
    }

    suspend fun clearExpiredTweets(conditionTime: Long) {
        localDataSource.clearExpiredTweets(conditionTime)
    }

    fun cancelStreamedCall() = remoteDataSource.cancelStreamedCall()

    suspend fun clearRules() {
        val rules = localDataSource.getRuleIds()
        if (rules.isNotEmpty()) {
            runInIO { remoteDataSource.deleteRule(rules) }
            localDataSource.clearRules()
        }
    }

    suspend fun addRule(query: String) {
        val ruleIds = runInIO { remoteDataSource.addRule(query) }
        localDataSource.saveRules(ruleIds)
    }

    private suspend fun <T> runInIO(block: suspend () -> T) = withContext(Dispatchers.IO) {
        block()
    }
}