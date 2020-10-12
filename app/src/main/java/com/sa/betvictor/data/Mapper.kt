package com.sa.betvictor.data

interface Mapper<Domain, Dto> {
    fun mapToDto(domain: Domain): Dto
    fun mapFromDto(dto: Dto): Domain
}