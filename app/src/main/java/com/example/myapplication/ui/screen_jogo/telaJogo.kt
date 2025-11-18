package com.example.myapplication.ui.screen_jogo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.utils.evaluateExpression
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun telaJogo(
    viewModel: JogoViewModel, // 2. Recebe o JogoViewModel
    onNavigateBack: () -> Unit
) {
    // 3. "Escuta" o uiState do JogoViewModel
    val uiState by viewModel.uiState.collectAsState()

    val operatorLabels = listOf("+", "-", "÷", "x")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(color = Color.White)
            }
            uiState.errorMessage != null -> {
                Text(text = uiState.errorMessage!!, color = Color.Red, fontSize = 20.sp, textAlign = TextAlign.Center)
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    //Barra Superior
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onNavigateBack,
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color.White)
                        ) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                        }
                    }

                    //Display da Expressão
                    Text(
                        text = uiState.expressaoAtual.ifEmpty { "Forme 10" },
                        modifier = Modifier.fillMaxWidth().offset(y = (-125).dp),
                        fontSize = 48.sp, color = Color.White, textAlign = TextAlign.Center
                    )

                    //Painel de Botões
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Botões de Número
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            uiState.numerosDoNivel.forEach { label ->
                                val isUsed = uiState.numerosUsados.contains(label)
                                Button(
                                    onClick = { viewModel.onNumeroClick(label) }, // Avisa o ViewModel
                                    enabled = !isUsed,
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isUsed) Color.Gray else Color.Transparent, disabledContainerColor = Color.Gray.copy(alpha = 0.5f)),
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(text = label, fontSize = 20.sp, color = Color.White)
                                }
                            }
                        }
                        // Botões de Operador
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            operatorLabels.forEach { label ->
                                OutlinedButton(
                                    onClick = { viewModel.onOperatorClick(label) }, // Avisa o ViewModel
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, Color.White),
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(text = label, fontSize = 20.sp, color = Color.White)
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(0.7f), color = Color.Gray)

                        // Botões de Controle
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { viewModel.onLimparClick() }, // Avisa o ViewModel
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) { Text("Limpar", color = Color.White) }

                            Button(
                                onClick = { viewModel.onApagarClick() }, // Avisa o ViewModel
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) { Icon(Icons.Filled.ArrowBack, contentDescription = "Apagar", tint = Color.White) }
                        }

                        // Botão de fazer juegada
                        Button(
                            onClick = { viewModel.verificarResultado() }, // Avisa o ViewModel
                            enabled = uiState.numerosUsados.size == 4 && uiState.resultadoFinal.isEmpty()
                        ) {
                            Text("FAZER JOGADA", fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        //Overlay de Resultado
        if (uiState.resultadoFinal.isNotEmpty()) {
            val vitoria = uiState.vitoria
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = uiState.resultadoFinal, fontSize = 100.sp, color = Color.White, textAlign = TextAlign.Center)
                    Text(text = if (vitoria) "Você Venceu!" else "Não foi 10!", fontSize = 40.sp, color = if (vitoria) Color.Green else Color.Red)
                }
            }
        }
    }
}