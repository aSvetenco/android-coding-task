package com.sa.betvictor.data

import com.sa.betvictor.data.local.dao.RuleDao
import com.sa.betvictor.data.local.dao.TweetDao
import com.sa.betvictor.data.local.entity.RuleEntity
import com.sa.betvictor.data.local.entity.TweetEntity
import com.sa.betvictor.data.remote.TweetRemoteClient
import com.sa.betvictor.data.remote.TweetRemoteDataSource
import com.sa.betvictor.domain.Tweet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TweetRepository(
    private val remoteDataSource: TweetRemoteClient,
    private val tweetDao: TweetDao,
    private val ruleDao: RuleDao
) : TweetRemoteDataSource.OnTweetsLoadedListener {

    fun getTweets() = tweetDao.getTweets().map { it.map { entity -> Tweet(entity.id, entity.tweet)} }

    suspend fun fetchTweets(query: String) =
        withContext(Dispatchers.IO) {
            clearRules()
            addRule(query)
            remoteDataSource.getTweets(this@TweetRepository)
        }

    private suspend fun clearRules() {
        val rules = ruleDao.getRules()
        if (rules.isNotEmpty()) {
            remoteDataSource.deleteRule(rules.map { it.id })
            ruleDao.deleteAll()
        }
    }

    private suspend fun addRule(query: String) {
        val rules = remoteDataSource.addRule(query)
        ruleDao.insertRules(rules.map { RuleEntity(it, "") })
    }

    override suspend fun onTweetsLoaded(tweets: List<Tweet>) {
        tweetDao.insertTweets(
            tweets.map { TweetEntity(it.id, it.text, System.currentTimeMillis()) }
        )
    }

}