package com.sa.betvictor.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sa.betvictor.data.local.database.entity.TweetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TweetDao {

    @Query("SELECT * FROM Tweets")
    fun getTweets(): Flow<List<TweetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTweets(tweets: List<TweetEntity>)

    @Query("DELETE FROM Tweets WHERE createdAt <:conditionTime")
    suspend fun deleteExpiredTweets(conditionTime: Long): Int
}
