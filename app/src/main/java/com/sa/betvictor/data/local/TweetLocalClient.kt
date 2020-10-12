package com.sa.betvictor.data.local

import com.sa.betvictor.domain.Tweet
import kotlinx.coroutines.flow.Flow

interface TweetLocalClient {

    fun getTweets(): Flow<List<Tweet>>

    suspend fun saveTweets(tweets: List<Tweet>)

    suspend fun deleteExpiredTweets(conditionTime: Long)

    suspend fun getRuleIds(): List<String>

    suspend fun saveRules(rules: List<String>)

    suspend fun clearRules()
}
