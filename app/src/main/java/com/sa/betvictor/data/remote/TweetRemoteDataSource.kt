package com.sa.betvictor.data.remote

import android.util.Log
import com.google.gson.Gson
import com.sa.betvictor.domain.Tweet
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

    override suspend fun getTweets(tweetLoaded: (List<Tweet>) -> Unit) {
        val response = api.getTweets()
        val source = response.source()
        val buffer = Buffer()
        val stream = StringBuilder()
        while (true) {
            if (source.read(buffer, 2560) == -1L) break
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
                tweetLoaded(parseTweetJson(tweets))
            }
        }
    }

    private fun parseTweetJson(tweets: List<String>) =
        try {
            tweets.map { gson.fromJson(it, TweetResponse::class.java).tweet }
        } catch (e: Throwable) {
            Log.e("JSON_FAILED", tweets.toString())
            listOf()
        }
}
