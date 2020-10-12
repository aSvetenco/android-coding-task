package com.sa.betvictor.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Tweets")
data class TweetEntity(
    @PrimaryKey
    val id: String,
    val tweet: String,
    val createdAt: Long
)