package com.sa.betvictor.data.remote

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming

interface Api {

    @POST("$VERSION/tweets/search/stream/rules")
    suspend fun addRule(@Body request: AddRuleRequest): AddRuleResponse

    @POST("$VERSION/tweets/search/stream/rules")
    suspend fun deleteRule(@Body request: DeleteRuleRequest): DeleteRuleResponse

    @Streaming
    @GET("$VERSION/tweets/search/stream")
    suspend fun getTweets(): ResponseBody

    private companion object {
        private const val VERSION = "2"
    }
}