package com.example.myapplication.ui.screen_adm

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.local.entities.LevelEntity
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.Stack
import com.example.myapplication.utils.evaluateExpression

// ASSINATURA DA FUNÇÃO
@Composable
fun AdmScreen(
    viewModel: AdmViewModel,
    onNavigateBack: () -> Unit
) {
    // COLETA DE ESTADO
    val uiState by viewModel.uiState.collectAsState()
    val levels by viewModel.levelsList.collectAsState() // Lista do Room
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C))
    ){

        // LÓGICA DE NAVEGAÇÃO INTERNA
        if (!uiState.isTestingLevel) {
            // Formulário de Criação e Lista do CRUD
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopBarAdm(onNavigateBack = onNavigateBack)

                Text(
                    text = "Criar Novo Nível",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                // TEXTFIELDS
                NumberTextField(value = uiState.primeiro, onValueChange = viewModel::onPrimeiroChange, label = "Primeiro Número")
                NumberTextField(value = uiState.segundo, onValueChange = viewModel::onSegundoChange, label = "Segundo Número")
                NumberTextField(value = uiState.terceiro, onValueChange = viewModel::onTerceiroChange, label = "Terceiro Número")
                NumberTextField(value = uiState.quarto, onValueChange = viewModel::onQuartoChange, label = "Quarto Número")

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÃO DE TESTE
                Button(
                    onClick = {
                        if (uiState.primeiro.isNotBlank() && uiState.segundo.isNotBlank() && uiState.terceiro.isNotBlank() && uiState.quarto.isNotBlank()) {
                            viewModel.onTestLevelClicked()
                        } else {
                            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Testar Nível")
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color.Gray)
                Text("Níveis Salvos (CRUD)", style = MaterialTheme.typography.headlineSmall, color = Color.White)

                // LISTA DE NÍVEIS
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(levels) { level ->
                        LevelItem(
                            level = level,
                            onDelete = {
                                viewModel.onDeleteLevel(level) // Delete do CRUD
                            }
                        )
                    }
                }
            }
        } else {
            // TELA 2: Tela de Teste
            LevelTestScreen(
                numeros = listOf(uiState.primeiro, uiState.segundo, uiState.terceiro, uiState.quarto),
                onLevelWon = {
                    viewModel.onLevelTestWon()
                },
                onBackClick = {
                    viewModel.onCancelTest()
                }
            )
        }

        // LOADING
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

// Item da Lista para o CRUD
@Composable
fun LevelItem(level: LevelEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "ID: ${level.id} - [${level.primeiro}, ${level.segundo}, ${level.terceiro}, ${level.quarto}]")
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}


// Tela de Teste
@Composable
fun LevelTestScreen(
    numeros: List<String>,
    onLevelWon: () -> Unit,
    onBackClick: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val numberLabels = numeros
    val operatorLabels = listOf("+", "-", "÷", "x")
    val numbersInExpression = remember(text) {
        text.split(Regex("[+\\-x÷]")).filter { it.isNotEmpty() }
    }
    val finalResult = if (text.isNotEmpty() && text.last().isDigit() && numbersInExpression.size == 4) {
        evaluateExpression(text)
    } else { "" }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Cancelar Teste", tint = Color.White)
                }
            }
            Text(
                text = text.ifEmpty { "Forme 10" },
                modifier = Modifier.fillMaxWidth(),
                fontSize = 48.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    numberLabels.forEach { label ->
                        val isUsed = numbersInExpression.count { it == label } >= numberLabels.count { it == label }
                        Button(
                            onClick = {
                                val canAppendNumber = text.isEmpty() || !text.last().isDigit()
                                if (canAppendNumber) {
                                    text += label
                                }
                            },
                            enabled = !isUsed,
                        ) {
                            Text(text = label, fontSize = 20.sp, color = Color.White)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    operatorLabels.forEach { label ->
                        OutlinedButton(
                            onClick = {
                                if (text.isNotEmpty() && text.last().isDigit()) {
                                    text += label
                                }
                            },
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color.White),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(text = label, fontSize = 20.sp, color = Color.White)
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(0.7f), color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { text = "" },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text("Limpar", color = Color.White)
                    }
                    Button(
                        onClick = {
                            if (text.isNotEmpty()) {
                                val lastNum = text.split(Regex("[+\\-x÷]")).lastOrNull { it.isNotEmpty() }
                                if (text.last().isDigit() && lastNum != null) {
                                    text = text.removeSuffix(lastNum)
                                } else {
                                    text = text.dropLast(1)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Apagar", tint = Color.White)
                    }
                }
            }
        }
        if (finalResult.isNotEmpty()) {
            val resultadoComoNumero = finalResult.toDoubleOrNull()
            val vitoria = resultadoComoNumero == 10.0
            if (vitoria) {
                LaunchedEffect(Unit) {
                    onLevelWon()
                }
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = finalResult,
                        fontSize = 100.sp,
                        color = if (vitoria) Color.Green else Color.Red
                    )
                    Text(
                        text = if (vitoria) "Nível Válido!" else "Não foi 10!",
                        fontSize = 40.sp,
                        color = if (vitoria) Color.Green else Color.Red
                    )
                }
            }
        }
    }
}



@Composable
fun NumberTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.DarkGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun TopBarAdm(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onNavigateBack,
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.White)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White,
            )
        }
    }
}




@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AdmScreenPreview() {
    MyApplicationTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C))
        ) {
        }
    }
}