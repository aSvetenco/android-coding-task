package com.sa.betvictor.domain

import com.google.gson.annotations.SerializedName

data class Tweet(
    val id: String = "",
    val text: String = ""
)

data class TweetResponse(@SerializedName("data") val tweet: Tweet = Tweet())