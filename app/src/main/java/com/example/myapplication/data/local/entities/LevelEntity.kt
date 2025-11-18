package com.example.myapplication.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

//2ª Entidade do Room
@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val primeiro: String,
    val segundo: String,
    val terceiro: String,
    val quarto: String
)