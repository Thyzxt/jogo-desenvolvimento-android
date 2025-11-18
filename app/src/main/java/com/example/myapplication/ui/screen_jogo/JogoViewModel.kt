package com.example.myapplication.ui.screen_jogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.GameRepository
import com.example.myapplication.utils.evaluateExpression
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.myapplication.data.local.entities.RankingEntity

// 1. O ESTADO DA UI COM O CAMPO PONTUACAO
data class JogoUiState(
    val nivelAtual: Int = 1,
    val pontuacao: Int = 0,
    val expressaoAtual: String = "",
    val numerosDoNivel: List<String> = emptyList(),
    val numerosUsados: List<String> = emptyList(),
    val resultadoFinal: String = "",
    val vitoria: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isGameOver: Boolean = false,
    val playerName: String = "Jogador Padrão"
)

// 2. A CLASSE DO VIEWMODEL
class JogoViewModel(private val repository: GameRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(JogoUiState())
    val uiState = _uiState.asStateFlow()

    init {
        carregarNivel(1)
    }

    // Lógica de carregamento
    fun carregarNivel(nivel: Int) {
        viewModelScope.launch {
            // Preserva o nome do jogador e a pontuação anterior
            val oldPontuacao = _uiState.value.pontuacao
            val oldName = _uiState.value.playerName

            _uiState.value = JogoUiState(
                isLoading = true,
                nivelAtual = nivel,
                pontuacao = oldPontuacao,
                playerName = oldName
            )

            try {
                val numeros = repository.getNivelFromFirebase(nivel)

                _uiState.value = JogoUiState(
                    nivelAtual = nivel,
                    pontuacao = oldPontuacao, // Mantém o score
                    playerName = oldName,
                    numerosDoNivel = numeros,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // Funções de interação (sem mudanças)
    fun onNumeroClick(numero: String) {
        val state = _uiState.value
        if (state.isGameOver) return
        val podeAnexar = state.expressaoAtual.isEmpty() || !state.expressaoAtual.last().isDigit()

        if (!state.numerosUsados.contains(numero) && podeAnexar) {
            _uiState.update {
                it.copy(
                    expressaoAtual = it.expressaoAtual + numero,
                    numerosUsados = it.numerosUsados + numero,
                    resultadoFinal = ""
                )
            }
        }
    }

    fun onOperatorClick(operador: String) {
        val state = _uiState.value
        if (state.expressaoAtual.isNotEmpty() && state.expressaoAtual.last().isDigit()) {
            _uiState.update { it.copy(expressaoAtual = it.expressaoAtual + operador) }
        }
    }

    fun onLimparClick() {
        _uiState.update {
            it.copy(
                expressaoAtual = "",
                numerosUsados = emptyList(),
                resultadoFinal = ""
            )
        }
    }

    fun onApagarClick() {
        val state = _uiState.value
        if (state.expressaoAtual.isEmpty()) return

        var novaExpressao = state.expressaoAtual
        var novosUsados = state.numerosUsados

        if (!state.expressaoAtual.last().isDigit()) {
            novaExpressao = state.expressaoAtual.dropLast(1)
        } else {
            val ultimoNumeroUsado = state.numerosUsados.lastOrNull()
            if (ultimoNumeroUsado != null) {
                novaExpressao = state.expressaoAtual.removeSuffix(ultimoNumeroUsado)
                novosUsados = state.numerosUsados.dropLast(1)
            }
        }

        _uiState.update { it.copy(expressaoAtual = novaExpressao, numerosUsados = novosUsados, resultadoFinal = "") }
    }

    fun verificarResultado() {
        val state = _uiState.value
        if (state.numerosUsados.size != 4 || state.isGameOver) return

        try {
            val resultado = evaluateExpression(state.expressaoAtual)
            val vitoria = (resultado == "10")

            _uiState.update { it.copy(resultadoFinal = resultado, vitoria = vitoria) }

            if (vitoria) {
                val novoScore = state.pontuacao + 1 // INCREMENTA O SCORE
                _uiState.update { it.copy(pontuacao = novoScore) }
                avancarNivel()
            } else if (state.nivelAtual >= 1) { // Se errou, FIM DE JOGO
                _uiState.update { it.copy(isGameOver = true, resultadoFinal = "Fim de Jogo") }
                salvarPontuacao() // ⚠️ Salva o score
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(resultadoFinal = "Erro") }
        }
    }

    // 3. FUNÇÃO QUE SALVA O SCORE NO BANCO
    private fun salvarPontuacao() {
        val state = _uiState.value
        if (state.pontuacao > 0) {
            viewModelScope.launch {
                repository.saveScore(
                    RankingEntity(
                        playerName = state.playerName,
                        score = state.pontuacao,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    private fun avancarNivel() {
        viewModelScope.launch {
            delay(2000)
            val proximoNivel = _uiState.value.nivelAtual + 1
            carregarNivel(proximoNivel)
        }
    }
}