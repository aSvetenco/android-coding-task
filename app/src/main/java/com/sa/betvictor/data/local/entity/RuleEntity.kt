package com.sa.betvictor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Rules")
data class RuleEntity(
    @PrimaryKey
    val id: String,
    val value: String
)