package com.sa.betvictor.data.remote

import android.util.Log
import com.google.gson.Gson
import com.sa.betvictor.domain.Tweet
import com.sa.betvictor.domain.TweetResponse
import okio.Buffer
import java.nio.charset.Charset

class TweetRemoteDataSource(
    private val api: Api,
    private val gson: Gson
) : TweetRemoteClient {

    override suspend fun addRule(query: String): String {
        val request = AddRuleRequest(listOf(Rule(value = query)))
        val rules = api.addRule(request).data
        return if (rules.isEmpty()) "" else rules[0].id ?: ""
    }

    override suspend fun deleteRule(id: String): String {
        val request = DeleteRuleRequest(DeletedRule(listOf(id)))
        val meta = api.deleteRule(request).meta
        return if (meta.summary.deleted == 1) id else ""
    }

    override suspend fun getTweets(tweetLoaded: (Tweet) -> Unit) {
        val response = api.getTweets()
        val source = response.source()
        val buffer = Buffer()
        val stream = StringBuilder()
        while (true) {
            if (source.read(buffer, 1024) == -1L) break
            stream.append(buffer.readString(Charset.defaultCharset()))
            if (stream.contains("}\r\n")) {
                val textStream = stream.toString()
                val json = textStream.substringBefore("\r\n")
                val remainder = textStream.substringAfter("\r\n")
                stream.setLength(0)
                stream.append(remainder)
                tweetLoaded(parseTweetJson(json))
            }
        }
    }

    private fun parseTweetJson(json: String) =
        try {
            gson.fromJson(json, TweetResponse::class.java).tweet
        } catch (e: Throwable) {
            Log.e("JSON_FAILED", json)
            Tweet()
        }
}
