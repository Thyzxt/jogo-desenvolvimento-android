package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import com.example.myapplication.evaluateExpression

class Jogo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GameScreen(navController = rememberNavController())
            }
        }
    }
}

// --- PASSO 1: O NOVO GameScreen se torna o "Container Inteligente" ---
@Composable
fun GameScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    testLevelId: String? = null
) {
    var isFirebaseReady by remember { mutableStateOf(false) }
    var connectionError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = true) {
        try {
            FirebaseFirestore.getInstance().collection("status_check").document("1").get().await()
            isFirebaseReady = true
        } catch (e: Exception) {
            connectionError = "Falha ao conectar. Verifique sua internet."
        }
    }

    if (isFirebaseReady) {
        GameScreenContent(
            navController = navController,
            modifier = modifier,
            testLevelId = testLevelId
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF2C2C2C)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (connectionError == null) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Conectando...", color = Color.White, fontSize = 18.sp)
                } else {
                    Text(text = connectionError!!, color = Color.Red, fontSize = 18.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

// --- PASSO 2: O conteúdo do jogo é movido para este Composable ---
@Composable
fun GameScreenContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    testLevelId: String? = null
) {
    var nivelAtual by remember { mutableStateOf(testLevelId?.toIntOrNull() ?: 1) }
    var text by remember { mutableStateOf("") }
    var usedNumbers by remember { mutableStateOf<List<String>>(emptyList()) }
    var primeiro by remember { mutableStateOf("") }
    var segundo by remember { mutableStateOf("") }
    var terceiro by remember { mutableStateOf("") }
    var quarto by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    // --- CÓDIGO CORRIGIDO (Início) ---
    // Efeito que busca os dados do nível no Firebase
    LaunchedEffect(nivelAtual) {
        isLoading = true
        loadError = null
        text = ""
        usedNumbers = emptyList()

        try {
            // Usa await() que é mais limpo e funciona bem com try-catch
            val doc = FirebaseFirestore.getInstance()
                .collection("niveis")
                .document(nivelAtual.toString())
                .get()
                .await() // <-- MUDANÇA AQUI

            if (doc != null && doc.exists()) {
                // Função auxiliar SEGURA para ler os dados
                // Isso funciona se o dado no Firebase for Number (Long/Double) OU String
                fun safeGetString(field: String): String {
                    val value = doc.get(field)
                    return when (value) {
                        is Number -> value.toLong().toString() // Converte Long ou Double
                        is String -> value // Aceita String
                        else -> ""
                    }
                }

                primeiro = safeGetString("primeiro")
                segundo = safeGetString("segundo")
                terceiro = safeGetString("terceiro")
                quarto = safeGetString("quarto")

                // Verifica se os dados realmente vieram
                if (primeiro.isEmpty() || segundo.isEmpty() || terceiro.isEmpty() || quarto.isEmpty()) {
                    loadError = "Dados do nível $nivelAtual incompletos."
                }
            } else {
                loadError = "Nível $nivelAtual não encontrado."
            }

        } catch (e: Exception) {
            // Este catch agora captura erros de conexão (do await)
            // E erros de leitura de dados (do safeGetString ou doc.get)
            loadError = "Erro ao carregar o nível: ${e.message}"

        } finally {
            // Garante que o loading sempre termine
            isLoading = false
        }
    }
    // --- CÓDIGO CORRIGIDO (Fim) ---

    // Passa os dados para o Composable de UI "ButtonGrid"
    ButtonGrid(
        modifier = modifier,
        navController = navController,
        text = text,
        nivelAtual = nivelAtual,
        numberLabels = listOf(primeiro, segundo, terceiro, quarto),
        usedNumbers = usedNumbers,
        isLoading = isLoading,
        loadError = loadError,
        onTextChange = { newText -> text = newText },
        onUsedNumbersChange = { newNumbers -> usedNumbers = newNumbers },
        onNivelChange = { newNivel ->
            if (testLevelId == null) {
                nivelAtual = newNivel
            } else {
                navController.popBackStack()
            }
        }
    )
}

// --- PASSO 3: O ButtonGrid e o Preview ---
@Composable
fun ButtonGrid(
    modifier: Modifier = Modifier,
    navController: NavController?,
    onBackPress: (() -> Unit)? = null,
    text: String,
    nivelAtual: Int,
    numberLabels: List<String>,
    usedNumbers: List<String>,
    isLoading: Boolean,
    loadError: String?,
    onTextChange: (String) -> Unit,
    onUsedNumbersChange: (List<String>) -> Unit,
    onNivelChange: (Int) -> Unit
) {
    val operatorLabels = listOf("+", "-", "÷", "x")

    // --- CÓDIGO CORRIGIDO (Início) ---
    val finalResult = if (text.isNotEmpty() && text.last().isDigit() && usedNumbers.size == 4) {
        // Adiciona um try-catch para proteger contra falhas de cálculo
        try {
            evaluateExpression(text)
        } catch (e: Exception) {
            "Erro" // Mostra "Erro" se o cálculo falhar (ex: divisão por zero)
        }
    } else {
        ""
    }
    // --- CÓDIGO CORRIGIDO (Fim) ---

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(color = Color.White)
            }
            loadError != null -> {
                Text(text = loadError, color = Color.Red, fontSize = 20.sp, textAlign = TextAlign.Center)
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (onBackPress != null) {
                                    onBackPress()
                                } else {
                                    navController?.popBackStack()
                                }
                            },
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color.White)
                        ) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                        }
                    }
                    Text(text = text.ifEmpty { "Forme 10" }, modifier = Modifier.fillMaxWidth().offset(y = (-125).dp), fontSize = 48.sp, color = Color.White, textAlign = TextAlign.Center)
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            numberLabels.forEach { label ->
                                if (label.isNotEmpty()) {
                                    val isUsed = usedNumbers.contains(label)
                                    Button(
                                        onClick = {
                                            val canAppendNumber = text.isEmpty() || !text.last().isDigit()
                                            if (!isUsed && canAppendNumber) {
                                                onTextChange(text + label)
                                                onUsedNumbersChange(usedNumbers + label)
                                            }
                                        },
                                        enabled = !isUsed,
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isUsed) Color.Gray else Color.Transparent, disabledContainerColor = Color.Gray.copy(alpha = 0.5f)),
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Text(text = label, fontSize = 20.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            operatorLabels.forEach { label ->
                                OutlinedButton(
                                    onClick = { if (text.isNotEmpty() && text.last().isDigit()) { onTextChange(text + label) } },
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
                                onClick = {
                                    onTextChange("")
                                    onUsedNumbersChange(emptyList())
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) { Text("Limpar", color = Color.White) }
                            Button(
                                onClick = {
                                    if (text.isNotEmpty()) {
                                        if (!text.last().isDigit()) {
                                            onTextChange(text.dropLast(1))
                                        } else {
                                            val lastUsedNumber = usedNumbers.lastOrNull()
                                            if (lastUsedNumber != null) {
                                                onTextChange(text.removeSuffix(lastUsedNumber))
                                                onUsedNumbersChange(usedNumbers.dropLast(1))
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) { Icon(Icons.Filled.ArrowBack, contentDescription = "Apagar", tint = Color.White) }
                        }
                    }
                }
            }
        }
        if (finalResult.isNotEmpty() && finalResult != "Erro") { // Modificado para não entrar no delay se der "Erro"
            val resultadoComoNumero = finalResult.toIntOrNull()
            val vitoria = resultadoComoNumero == 10
            if (vitoria) {
                LaunchedEffect(nivelAtual) {
                    delay(2000)
                    onNivelChange(nivelAtual + 1)
                }
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = finalResult, fontSize = 100.sp, color = Color.White, textAlign = TextAlign.Center)
                    Text(text = if (vitoria) "Você Venceu!" else "Não foi 10!", fontSize = 40.sp, color = if (vitoria) Color.Green else Color.Red)
                }
            }
        } else if (finalResult == "Erro") { // Mostra o erro de cálculo
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Expressão Inválida", fontSize = 40.sp, color = Color.Red, textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun GamePreview() {
    MyApplicationTheme {
        ButtonGrid(
            navController = rememberNavController(),
            text = "10+2",
            nivelAtual = 1,
            numberLabels = listOf("10", "2", "5", "3"),
            usedNumbers = listOf("10", "2"),
            isLoading = false,
            loadError = null,
            onTextChange = {},
            onUsedNumbersChange = {},
            onNivelChange = {}
        )
    }
}