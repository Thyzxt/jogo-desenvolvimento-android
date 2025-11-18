package com.example.myapplication.ui.screen_adm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.entities.LevelEntity
import com.example.myapplication.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class AdmUiState(
    val primeiro: String = "",
    val segundo: String = "",
    val terceiro: String = "",
    val quarto: String = "",
    val isLoading: Boolean = false,
    val isTestingLevel: Boolean = false,
    val levelsFromDb: List<LevelEntity> = emptyList() // Lista para o CRUD
)

class AdmViewModel(private val repository: GameRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AdmUiState())
    val uiState = _uiState.asStateFlow()

    //Room (Flow)
    val levelsList = repository.getAllLevels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //Funções que a UI vai chamar

    fun onPrimeiroChange(value: String) = _uiState.update { it.copy(primeiro = value) }
    fun onSegundoChange(value: String) = _uiState.update { it.copy(segundo = value) }
    fun onTerceiroChange(value: String) = _uiState.update { it.copy(terceiro = value) }
    fun onQuartoChange(value: String) = _uiState.update { it.copy(quarto = value) }

    fun onTestLevelClicked() {
        val state = _uiState.value
        if (state.primeiro.isNotBlank() && state.segundo.isNotBlank() && state.terceiro.isNotBlank() && state.quarto.isNotBlank()) {
            _uiState.update { it.copy(isTestingLevel = true) }
        } else {
            // TODO: Mandar um evento de Toast
        }
    }

    //Função chamada pela scren quando o teste do nível dá certo
    fun onLevelTestWon() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // O ViewModel chama o Repositório
                repository.saveNewLevel(state.primeiro, state.segundo, state.terceiro, state.quarto)
                // Limpa os campos e volta para a tela do formulário
                _uiState.value = AdmUiState() // Reseta tudo, ok? Tudo.
            } catch (e: Exception) {
                // TODO: Enviar evento de erro
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onCancelTest() {
        _uiState.update { it.copy(isTestingLevel = false) }
    }

    fun onDeleteLevel(level: LevelEntity) {
        viewModelScope.launch {
            repository.deleteLevel(level)
        }
    }
}