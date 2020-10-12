package com.sa.betvictor.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sa.betvictor.data.local.database.dao.RuleDao
import com.sa.betvictor.data.local.database.dao.TweetDao
import com.sa.betvictor.data.local.database.entity.RuleEntity
import com.sa.betvictor.data.local.database.entity.TweetEntity

@Database(entities = [TweetEntity::class, RuleEntity::class], version = 1, exportSchema = false)
abstract class TweetsDatabase : RoomDatabase() {

    abstract fun tweetDao(): TweetDao
    abstract fun ruleDao(): RuleDao

    companion object {

        private const val DATABASE_NAME = "TweetsDatabase.db"

        @Volatile
        private var instance: TweetsDatabase? = null

        fun getInstance(context: Context): TweetsDatabase {
            return instance ?: synchronized(this) { buildDatabase(context).also { instance = it } }
        }

        fun clearTables() {
            instance?.clearAllTables()
        }

        private fun buildDatabase(context: Context): TweetsDatabase {
            return Room.databaseBuilder(context, TweetsDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

}