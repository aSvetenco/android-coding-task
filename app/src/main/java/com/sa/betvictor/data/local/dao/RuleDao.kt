package com.sa.betvictor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sa.betvictor.data.local.entity.RuleEntity

@Dao
interface RuleDao {

    @Query("SELECT * FROM Rules")
    suspend fun getRules(): List<RuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: RuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<RuleEntity>)

    @Query("DELETE FROM Rules")
    suspend fun deleteAll()
}