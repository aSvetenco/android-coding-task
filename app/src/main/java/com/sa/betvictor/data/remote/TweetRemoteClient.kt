package com.sa.betvictor.data.remote

import com.google.gson.annotations.SerializedName
import com.sa.betvictor.domain.Tweet
import kotlinx.coroutines.flow.Flow

interface TweetRemoteClient {
    suspend fun addRule(query: String): List<String>
    suspend fun deleteRule(ids: List<String>): List<String>
    suspend fun getTweets(listener: TweetRemoteDataSource.OnTweetsLoadedListener)
}

data class AddRuleRequest(val add: List<Rule> = listOf())

data class AddRuleResponse(val data: List<Rule> = listOf())

data class Rule(
    val value: String,
    val tag: String? = null,
    val id: String? = null
)

data class DeleteRuleRequest(val delete: DeletedRule = DeletedRule())

data class DeleteRuleResponse(val meta: DeleteMetaData = DeleteMetaData())

data class DeletedRule(val isd: List<String> = listOf())

data class DeleteMetaData(val summary: Summary = Summary())

data class Summary(
    val deleted: Int = 1,
    @SerializedName("not_deleted") val notDeleted: Int = 0
)

data class TweetResponse(@SerializedName("data") val tweet: Tweet = Tweet())
