package com.example.myapplication.ui.screen_cadastro // Seu pacote original

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun TelaCad(
    viewModel: CadastroViewModel,
    onNavigateBack: () -> Unit // Callback para navegar de volta
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.cadastroEvent.collect { event ->
            when (event) {
                is CadastroEvent.Success -> {
                    Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    onNavigateBack() // Navega de volta para o Login
                }
                is CadastroEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Criar Conta",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = uiState.email, // Lê o estado do ViewModel
            onValueChange = { viewModel.onEmailChange(it) }, // Avisa o ViewModel
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = uiState.password, // Lê o estado
            onValueChange = { viewModel.onPasswordChange(it) }, // Avisa o VM
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),

        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.confirmPassword, // Lê o estado
            onValueChange = { viewModel.onConfirmPasswordChange(it) }, // Avisa o VM
            label = { Text("Confirmar Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),

        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.onRegisterClicked()
            },
            enabled = !uiState.isLoading, // Desabilita o botão se estiver carregando
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Cadastrar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateBack) { // Chama a lambida
            Text("Já tem uma conta? Faça login", color = Color.Black)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaCadPreview() {
    MyApplicationTheme {

    }
}