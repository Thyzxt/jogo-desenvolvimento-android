package com.example.myapplication.ui.screen_cadastro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// O Estado da UI
data class CadastroUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false
)

// Eventos
sealed interface CadastroEvent {
    object Success : CadastroEvent
    data class Error(val message: String) : CadastroEvent
}

// 3. O ViewModel
class CadastroViewModel(private val repository: GameRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CadastroUiState())
    val uiState = _uiState.asStateFlow()

    private val _cadastroEvent = MutableSharedFlow<CadastroEvent>()
    val cadastroEvent = _cadastroEvent.asSharedFlow()

    // --- Funções que a UI vai chamar ---

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail.trim()) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    fun onConfirmPasswordChange(newPassword: String) {
        _uiState.update { it.copy(confirmPassword = newPassword) }
    }

    fun onRegisterClicked() {
        val state = _uiState.value

        // 1. Validação
        if (state.email.isBlank() || state.password.isBlank() || state.confirmPassword.isBlank()) {
            emitError("Por favor, preencha todos os campos.")
            return
        }
        if (state.password != state.confirmPassword) {
            emitError("As senhas não coincidem.")
            return
        }

        // Lógica de Cadastro
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Ativa o loadingue

            try {
                // O ViewModel SÓ CHAMA o repositório
                repository.registerUser(state.email, state.password)

                // Sucesso
                _cadastroEvent.emit(CadastroEvent.Success)

            } catch (e: Exception) {
                // Falha
                emitError(e.message ?: "Erro desconhecido")
            } finally {
                // Desativa o loading
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Função auxiliar para emitir erros
    private fun emitError(message: String) {
        viewModelScope.launch {
            _cadastroEvent.emit(CadastroEvent.Error(message))
        }
    }
}