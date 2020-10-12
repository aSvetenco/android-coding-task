package com.sa.betvictor.data.local

import com.sa.betvictor.data.local.database.dao.RuleDao
import com.sa.betvictor.data.local.database.dao.TweetDao
import com.sa.betvictor.data.local.database.entity.RuleEntity
import com.sa.betvictor.domain.Tweet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TweetLocalDataSource(
    private val tweetDao: TweetDao,
    private val ruleDao: RuleDao,
    private val tweetMapper: TweetEntityMapper
) : TweetLocalClient {

    override fun getTweets(): Flow<List<Tweet>> =
        tweetDao.getTweets().map { entities -> entities.map { tweetMapper.mapFromDto(it) } }

    override suspend fun saveTweets(tweets: List<Tweet>) {
        tweetDao.insertTweets(tweets.map { tweetMapper.mapToDto(it) })
    }

    override suspend fun clearExpiredTweets(conditionTime: Long) {
        tweetDao.deleteExpiredTweets(conditionTime)
    }

    override suspend fun getRuleIds(): List<String> = ruleDao.getRules().map { it.id }

    override suspend fun saveRules(rules: List<String>) {
        ruleDao.insertRules(rules.map { RuleEntity(it) })
    }

    override suspend fun clearRules() {
        ruleDao.deleteAll()
    }
}
