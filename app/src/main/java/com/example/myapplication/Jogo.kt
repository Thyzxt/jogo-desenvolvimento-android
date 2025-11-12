package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import java.util.Stack

class Jogo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(color = 0xFF2C2C2C) // Fundo escuro
                ) { innerPadding ->
                    ButtonGrid(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// --- Lógica de Cálculo Manual ---

private fun precedence(op: Char): Int {
    return when (op) {
        '+', '-' -> 1
        'x', '÷' -> 2
        else -> -1
    }
}

private fun applyOp(a: Double, b: Double, op: Char): Double {
    return when (op) {
        '+' -> a + b
        '-' -> a - b
        'x' -> a * b
        '÷' -> {
            if (b == 0.0) throw UnsupportedOperationException("Divisão por zero")
            a / b
        }
        else -> 0.0
    }
}

private fun evaluateExpression(expression: String): String {
    try {
        val values = Stack<Double>()
        val ops = Stack<Char>()
        var i = 0
        while (i < expression.length) {
            if (expression[i].isDigit()) {
                val buffer = StringBuilder()
                while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                    buffer.append(expression[i++])
                }
                values.push(buffer.toString().toDouble())
                i--
            } else if (expression[i] in "+-x÷") {
                while (!ops.empty() && precedence(ops.peek()) >= precedence(expression[i])) {
                    val val2 = values.pop()
                    val val1 = values.pop()
                    val op = ops.pop()
                    values.push(applyOp(val1, val2, op))
                }
                ops.push(expression[i])
            }
            i++
        }

        while (!ops.empty()) {
            val val2 = values.pop()
            val val1 = values.pop()
            val op = ops.pop()
            values.push(applyOp(val1, val2, op))
        }

        val result = values.pop()
        return if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            String.format(java.util.Locale.US, "%.2f", result)
        }
    } catch (e: Exception) {
        return ""
    }
}

// --- Fim da Lógica de Cálculo ---

@Composable
fun ButtonGrid(navController: NavController? = null, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    var nivelAtual by remember { mutableStateOf(1) }

    // Os números agora são estados que podem mudar
    var primeiro by remember { mutableStateOf("") }
    var segundo by remember { mutableStateOf("") }
    var terceiro by remember { mutableStateOf("") }
    var quarto by remember { mutableStateOf("") }

    // Este Effect é executado sempre que `nivelAtual` muda.
    LaunchedEffect(nivelAtual) {
        // --- CÓDIGO FIREBASE (GENÉRICO) ---
        // Este é o lugar para buscar os dados do nível no Firebase.
        // Você precisará do SDK do Firebase configurado no seu projeto.
        /*
        val db = Firebase.firestore
        db.collection("niveis").document(nivelAtual.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    primeiro = document.getString("primeiro") ?: "0"
                    segundo = document.getString("segundo") ?: "0"
                    terceiro = document.getString("terceiro") ?: "0"
                    quarto = document.getString("quarto") ?: "0"
                    text = "" // Limpa o campo de texto para a nova fase
                } else {
                    // Nível não encontrado, você pode tratar como fim de jogo
                }
            }
            .addOnFailureListener { exception ->
                // Tratar erro de conexão com o Firebase
            }
        */

        // --- CÓDIGO PROVISÓRIO (para o app funcionar sem Firebase) ---
        // Esta parte simula a busca de dados, gerando novos números.
        // SUBSTITUA esta lógica pelo código do Firebase acima.
        primeiro = (nivelAtual).toString()
        segundo = (nivelAtual + 1).toString()
        terceiro = (nivelAtual + 2).toString()
        quarto = (nivelAtual + 3).toString()
        text = ""
    }

    val numberLabels = listOf(primeiro, segundo, terceiro, quarto)
    val operatorLabels = listOf("+", "-", "÷", "x")

    val numbersInExpression = text.filter { it.isDigit() }.map { it.toString() }

    val finalResult = if (text.isNotEmpty() && text.last().isDigit() && numbersInExpression.size == 4) {
        evaluateExpression(text)
    } else {
        ""
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Coloca a expressão em cima e os botões embaixo
        ) {
            // Display para a expressão sendo construída
            Text(
                text = text.ifEmpty { "Forme 10" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp),
                fontSize = 48.sp,
                color = Color.White, // Cor do texto principal
                textAlign = TextAlign.Center
            )

            // Painel para todos os botões
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botões de número
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    numberLabels.forEach { label ->
                        if (label.isNotEmpty()) { // Só mostra o botão se o número já foi carregado
                            Button(
                                onClick = {
                                    val notUsedYet = !numbersInExpression.contains(label)
                                    val canAppendNumber = text.isEmpty() || !text.last().isDigit()
                                    if (notUsedYet && canAppendNumber) {
                                        text += label
                                    }
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(text = label, fontSize = 20.sp, color = Color.White)
                            }
                        }
                    }
                }

                // Botões de operador
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

                // Botões de controle
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
                                text = text.dropLast(1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Apagar", tint = Color.White)
                    }
                }
            }
        }

        // Overlay do resultado final
        if (finalResult.isNotEmpty()) {
            val resultadoComoNumero = finalResult.toIntOrNull()
            val vitoria = resultadoComoNumero == 10

            // Este Effect executa a lógica de vitória apenas uma vez por nível.
            if (vitoria) {
                LaunchedEffect(nivelAtual) {
                    delay(2000) // Espera 2s para o jogador ver a mensagem
                    nivelAtual++ // Avança para o próximo nível, o que vai disparar a busca de dados
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // 1. O resultado numérico
                    Text(
                        text = finalResult,
                        fontSize = 100.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    // 2. A mensagem de vitória ou tentativa
                    if (vitoria) {
                        Text(
                            text = "Você Venceu!",
                            fontSize = 40.sp,
                            color = Color.Green
                        )
                    } else {
                        Text(
                            text = "Não foi 10!",
                            fontSize = 40.sp,
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun ButtonGridPreview() {
    MyApplicationTheme {
        ButtonGrid(modifier = Modifier.fillMaxSize())
    }
}
