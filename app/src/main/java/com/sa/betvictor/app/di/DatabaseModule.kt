package com.sa.betvictor.app.di

import android.app.Application
import com.sa.betvictor.data.local.database.TweetsDatabase

class DatabaseModule(private val app: Application) {

    fun tweetDao() = tweetsDatabase().tweetDao()

    fun ruleDao() = tweetsDatabase().ruleDao()

    private fun tweetsDatabase() = TweetsDatabase.getInstance(app.applicationContext)
}
