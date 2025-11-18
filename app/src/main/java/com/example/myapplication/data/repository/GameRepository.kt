package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.RankingDao
import com.example.myapplication.data.local.entities.LevelEntity
import com.example.myapplication.data.local.entities.RankingEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(
    private val rankingDao: RankingDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    //Funções do Ranking
    fun getRanking(): Flow<List<RankingEntity>> {
        return rankingDao.getAllScores()
    }

    suspend fun saveScore(score: RankingEntity) {
        withContext(Dispatchers.IO) {
            rankingDao.insertScore(score)
        }
    }

    //Função do Jogo
    suspend fun getNivelFromFirebase(nivel: Int): List<String> {
        try {
            val doc = firestore
                .collection("niveis")
                .document(nivel.toString())
                .get()
                .await()

            if (doc != null && doc.exists()) {
                fun safeGetString(field: String): String {
                    val value = doc.get(field)
                    return when (value) {
                        is Number -> value.toLong().toString()
                        is String -> value
                        else -> ""
                    }
                }
                val p1 = safeGetString("primeiro")
                val p2 = safeGetString("segundo")
                val p3 = safeGetString("terceiro")
                val p4 = safeGetString("quarto")

                if (p1.isEmpty() || p2.isEmpty() || p3.isEmpty() || p4.isEmpty()) {
                    throw Exception("Dados do nível $nivel incompletos.")
                }
                return listOf(p1, p2, p3, p4)
            } else {
                throw Exception("Nível $nivel não encontrado.")
            }
        } catch (e: Exception) {
            throw Exception("Erro ao carregar o nível: ${e.message}")
        }
    }

    //FUNÇÃO DE LOGIN
    suspend fun loginUser(email: String, password: String): Boolean {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            return (email.lowercase() == "admin@admin.com")
        } catch (e: Exception) {
            throw Exception("E-mail ou senha inválidos.")
        }
    }

    // FUNÇÃO DE CADASTRO
    suspend fun registerUser(email: String, password: String) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw Exception(e.message ?: "Erro desconhecido ao criar conta.")
        }
    }

    //NOVAS FUNÇÕES DE CRUD DO ADM É TOP

    // Read
    fun getAllLevels(): Flow<List<LevelEntity>> {
        return rankingDao.getAllLevels()
    }

    // Delete
    suspend fun deleteLevel(level: LevelEntity) {
        // 1. Deleta do Room
        rankingDao.deleteLevelById(level.id)
        // 2. Deleta do Firebase usando id
        try {
            firestore.collection("niveis").document(level.id.toString()).delete().await()
        } catch (e: Exception) {
            e.printStackTrace() // Ignora erro do Firebase por enquanto
        }
    }

    // Create
    suspend fun saveNewLevel(primeiro: String, segundo: String, terceiro: String, quarto: String) {
        // Salva no Room
        val newLevelEntity = LevelEntity(
            primeiro = primeiro,
            segundo = segundo,
            terceiro = terceiro,
            quarto = quarto
        )
        rankingDao.insertLevel(newLevelEntity) // O Room vai gerar um id

        // Pega o ID que o Room acabou de criar
        val lastLevel = rankingDao.getLastLevel()
        val levelId = lastLevel.id

        // Salva no Firebase usando o same ID, understand?
        val nivelFirebase = hashMapOf(
            "primeiro" to primeiro,
            "segundo" to segundo,
            "terceiro" to terceiro,
            "quarto" to quarto
        )
        firestore.collection("niveis").document(levelId.toString())
            .set(nivelFirebase).await()
    }
}