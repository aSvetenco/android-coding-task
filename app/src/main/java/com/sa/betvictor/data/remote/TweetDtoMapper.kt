package com.sa.betvictor.data.remote

import com.sa.betvictor.data.Mapper
import com.sa.betvictor.domain.Tweet

class TweetDtoMapper : Mapper<Tweet, TweetDto> {

    override fun mapToDto(domain: Tweet): TweetDto =
        TweetDto(domain.id, domain.text)

    override fun mapFromDto(dto: TweetDto): Tweet =
        Tweet(dto.id, dto.text)
}
