package com.example.myapplication.data.local.dao
//package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.myapplication.data.local.entities.LevelEntity
import com.example.myapplication.data.local.entities.RankingEntity


@Dao
interface RankingDao {

    // Funções do Ranking
    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: RankingEntity)
    // Read

    @Query("SELECT * FROM ranking ORDER BY score DESC")
    fun getAllScores(): Flow<List<RankingEntity>>

    // FUNÇÕES DE CRUD DO NÍVEL

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevel(level: LevelEntity)

    // Read
    @Query("SELECT * FROM levels ORDER BY id ASC")
    fun getAllLevels(): Flow<List<LevelEntity>>

    // Delete
    @Query("DELETE FROM levels WHERE id = :levelId")
    suspend fun deleteLevelById(levelId: Int)

    // Update
    @Query("UPDATE levels SET primeiro = :p1, segundo = :p2, terceiro = :p3, quarto = :p4 WHERE id = :levelId")
    suspend fun updateLevel(levelId: Int, p1: String, p2: String, p3: String, p4: String)

    // Pega o último ID
    @Query("SELECT * FROM levels ORDER BY id DESC LIMIT 1")
    suspend fun getLastLevel(): LevelEntity
}

