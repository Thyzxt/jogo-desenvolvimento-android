package com.example.myapplication.data.local.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ranking")
data class RankingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val playerName: String,
    val score: Int,
    val timestamp: Long
)