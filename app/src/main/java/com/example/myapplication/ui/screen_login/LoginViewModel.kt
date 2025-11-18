package com.example.myapplication.ui.screen_login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.GameRepository
// O ViewModel NÃO importa mais NADA do Firebase!
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)

sealed interface LoginEvent {
    data class Success(val isAdmin: Boolean) : LoginEvent
    data class Error(val message: String) : LoginEvent
}

// O ViewModel agora é "limpo". Ele só fala com o Repositório.
class LoginViewModel(private val repository: GameRepository) : ViewModel() {

    // private val auth: FirebaseAuth = Firebase.auth
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent = _loginEvent.asSharedFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail.trim()) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    fun onLoginClicked() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _loginEvent.emit(LoginEvent.Error("Por favor, preencha e-mail e senha."))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Ativa o loading

            try {
                // 1. O ViewModel SÓ CHAMA o repositório
                val isAdmin = repository.loginUser(email, password)

                // 2. Sucesso!
                _loginEvent.emit(LoginEvent.Success(isAdmin = isAdmin))

            } catch (e: Exception) {
                // 3. Falha! O repositório lança a exceção e o VM a captura.
                _loginEvent.emit(LoginEvent.Error(e.message ?: "Erro desconhecido"))
            } finally {
                // 4. Desativa o loading (acontecendo em sucesso ou falha)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}