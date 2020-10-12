package com.sa.betvictor.data.remote

import android.util.Log
import com.google.gson.Gson
import com.sa.betvictor.domain.Tweet
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Call
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
        return rules.map { it.id ?: "" }
    }

    override suspend fun deleteRule(ids: List<String>): List<String> {
        val request = DeleteRuleRequest(DeletedRule(ids))
        val meta = api.deleteRule(request).meta
        return if (meta.summary.deleted == 1) ids else listOf()
    }

    override suspend fun getTweets(listener: OnTweetsLoadedListener) {
        val response = api.getTweets().body()
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
                listener.onTweetsLoaded(parseTweetJson(tweets))
            }
        }
    }

    override fun cancelStreamedCall() {
        steamedCall?.cancel()
    }

    private fun parseTweetJson(tweets: List<String>) =
        try {
            tweets.map {
                val dto = gson.fromJson(it, TweetResponse::class.java).data
                mapper.mapFromDto(dto)
            }
        } catch (e: Throwable) {
            Log.e("JSON_FAILED", tweets.toString())
            listOf()
        }

   private fun Call<ResponseBody>.body(): ResponseBody {
        steamedCall = this
        val response = execute()

        if (response.isSuccessful) return response.body()
            ?: throw Throwable("Response body is null")
        else throw Throwable(response.errorBody()?.string())
    }

    interface OnTweetsLoadedListener {
        suspend fun onTweetsLoaded(tweets: List<Tweet>)
    }
}
