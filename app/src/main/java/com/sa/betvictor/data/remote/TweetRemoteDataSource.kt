package com.sa.betvictor.data.remote

import com.google.gson.Gson
import com.sa.betvictor.domain.Tweet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Call
import retrofit2.await
import java.nio.charset.Charset

class TweetRemoteDataSource(
    private val api: Api,
    private val gson: Gson,
    private val mapper: TweetDtoMapper
) : TweetRemoteClient {

    private var steamedCall: Call<ResponseBody>? = null

    override suspend fun addRule(query: String): List<String> {
        val request = AddRuleRequest(listOf(RuleDto(value = query)))
        val rules = api.addRule(request).data
        return rules.map { it.id ?: "" }.filter { it.isNotEmpty() }
    }

    override suspend fun deleteRule(ids: List<String>) {
        val request = DeleteRuleRequest(DeletedRule(ids))
        api.deleteRule(request).meta
    }

    override suspend fun fetchTweets(): Flow<List<Tweet>> = flow {
        val response = api.getTweets().body()
        val source = response.source()
        val buffer = Buffer()
        val stream = StringBuilder()
        while (true) {
            if (source.read(buffer, BUFFER_SIZE) == -1L) break
            stream.append(buffer.readString(Charset.defaultCharset()))

            if (stream.contains("}\r\n")) {
                val textStream = stream.toString()
                stream.setLength(0)
                val tweets = textStream.split("\r\n").toMutableList()
                val last = tweets.last()

                if (!last.endsWith("\r\n")) {
                    tweets.remove(last)
                    stream.append(last)
                }
                emit(parseTweetJson(tweets))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun cancelStreamedCall() {
        steamedCall?.cancel()
    }

    private fun parseTweetJson(tweets: List<String>) =
        tweets.filter { it.isNotEmpty() }
            .map {
                val dto = gson.fromJson(it, TweetResponse::class.java).data
                mapper.mapFromDto(dto)
            }

    private suspend fun Call<ResponseBody>.body(): ResponseBody {
        steamedCall = this
        return await()
    }

    companion object {
        private const val BUFFER_SIZE = 1024L
    }
}
