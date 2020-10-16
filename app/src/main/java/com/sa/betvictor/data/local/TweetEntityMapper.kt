package com.sa.betvictor.data.local

import com.sa.betvictor.data.Mapper
import com.sa.betvictor.data.local.database.entity.TweetEntity
import com.sa.betvictor.domain.Tweet

class TweetEntityMapper : Mapper<Tweet, TweetEntity> {

    override fun mapFromDto(dto: TweetEntity): Tweet =
        Tweet(dto.id, dto.tweet)

    override fun mapToDto(domain: Tweet): TweetEntity =
        TweetEntity(domain.id, domain.text, System.currentTimeMillis())
}
