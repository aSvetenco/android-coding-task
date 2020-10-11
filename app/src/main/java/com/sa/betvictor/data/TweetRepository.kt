package com.sa.betvictor.data

import androidx.lifecycle.MutableLiveData
import com.sa.betvictor.data.remote.TweetRemoteClient
import com.sa.betvictor.domain.Tweet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TweetRepository(private val remoteDataSource: TweetRemoteClient) {

    private val list = mutableListOf<Tweet>()

    suspend fun getTweets(query: String, tweetData: MutableLiveData<List<Tweet>>) =
        withContext(Dispatchers.IO) {
            //   val deleted = remoteDataSource.deleteRule("")
            val rule = remoteDataSource.addRule(query)
            remoteDataSource.getTweets {
                list.addAll(it)
                val newList = ArrayList(list)
                tweetData.postValue(newList)
            }
        }
}