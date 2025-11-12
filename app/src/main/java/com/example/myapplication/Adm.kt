package com.example.myapplication

import android.util.Log // <-- IMPORTANTE: Adicione esta importação
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Stack

@Composable
fun AdmScreen(navController: NavController? = null) {
    var primeiro by remember { mutableStateOf("") }
    var segundo by remember { mutableStateOf("") }
    var terceiro by remember { mutableStateOf("") }
    var quarto by remember { mutableStateOf("") }

    var isTestingLevel by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (!isTestingLevel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Criar Novo Nível",
                style = MaterialTheme.typography.headlineMedium,
                // A cor foi garantida como branca para ser visível no fundo escuro.
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))

            NumberTextField(value = primeiro, onValueChange = { primeiro = it }, label = "Primeiro Número")
            NumberTextField(value = segundo, onValueChange = { segundo = it }, label = "Segundo Número")
            NumberTextField(value = terceiro, onValueChange = { terceiro = it }, label = "Terceiro Número")
            NumberTextField(value = quarto, onValueChange = { quarto = it }, label = "Quarto Número")

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (primeiro.isNotBlank() && segundo.isNotBlank() && terceiro.isNotBlank() && quarto.isNotBlank()) {
                        isTestingLevel = true
                    } else {
                        Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Testar e Salvar Nível")
            }
        }
    } else {
        LevelTestScreen(
            numeros = listOf(primeiro, segundo, terceiro, quarto),
            onLevelWon = {
                isLoading = true
            }
        )
    }

    if (isLoading) {
        LaunchedEffect(Unit) {
            // ADICIONADO: Log para saber que o salvamento começou.
            Log.d("AdmScreenDebug", "isLoading é true. Tentando salvar no Firebase...")

            val db = Firebase.firestore
            db.collection("niveis")
                .get()
                .addOnSuccessListener { documents ->
                    val novoNivelId = (documents.size() + 1).toString()
                    // ADICIONADO: Log para confirmar o ID do novo nível.
                    Log.d("AdmScreenDebug", "Níveis contados: ${documents.size()}. Novo ID será: $novoNivelId")

                    val novoNivel = hashMapOf(
                        "primeiro" to primeiro,
                        "segundo" to segundo,
                        "terceiro" to terceiro,
                        "quarto" to quarto
                    )

                    db.collection("niveis").document(novoNivelId)
                        .set(novoNivel)
                        .addOnSuccessListener {
                            // ADICIONADO: Log para confirmar o sucesso.
                            Log.d("AdmScreenDebug", "SUCESSO! Nível $novoNivelId salvo no Firebase.")
                            Toast.makeText(context, "Nível $novoNivelId salvo com sucesso!", Toast.LENGTH_LONG).show()
                            isLoading = false
                            isTestingLevel = false
                            primeiro = ""
                            segundo = ""
                            terceiro = ""
                            quarto = ""
                        }
                        .addOnFailureListener { e ->
                            // ADICIONADO: Log para capturar o erro exato ao salvar.
                            Log.e("AdmScreenDebug", "FALHA ao salvar o documento: ", e)
                            Toast.makeText(context, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
                            isLoading = false
                        }
                }
                .addOnFailureListener { e ->
                    // ADICIONADO: Log para capturar o erro ao contar os documentos.
                    Log.e("AdmScreenDebug", "FALHA ao contar os níveis: ", e)
                    Toast.makeText(context, "Erro ao contar níveis: ${e.message}", Toast.LENGTH_LONG).show()
                    isLoading = false
                }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

@Composable
fun LevelTestScreen(numeros: List<String>, onLevelWon: () -> Unit) {
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
            Text(
                text = text.ifEmpty { "Forme 10" },
                modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
                fontSize = 48.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            // ... (o resto da UI de LevelTestScreen permanece igual)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botões de número
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    numberLabels.forEach { label ->
                        val isUsed = numbersInExpression.contains(label)
                        Button(
                            onClick = {
                                val canAppendNumber = text.isEmpty() || !text.last().isDigit()
                                if (canAppendNumber) {
                                    text += label
                                }
                            },
                            enabled = !isUsed,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(text = label, fontSize = 20.sp, color = Color.White)
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
        if (finalResult.isNotEmpty()) {
            val resultadoComoNumero = finalResult.toDoubleOrNull()
            val vitoria = resultadoComoNumero == 10.0
            if (vitoria) {
                LaunchedEffect(Unit) {
                    // ADICIONADO: Log para confirmar que a vitória foi detectada.
                    Log.d("AdmScreenDebug", "Vitória detectada! Acionando onLevelWon...")
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

// O resto do seu arquivo (NumberTextField, funções de cálculo, Preview) pode continuar igual.
// ... (cole o resto do seu código aqui)
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

// --- Funções de Cálculo (Copiadas de Jogo.kt) ---

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
        '÷' -> if (b == 0.0) Double.NaN else a / b // Evita crash, retorna um resultado inválido
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
        return if (result.isNaN()) {
            "Erro" // Mostra "Erro" em caso de divisão por zero
        } else if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            String.format(java.util.Locale.US, "%.2f", result)
        }
    } catch (e: Exception) {
        return "Erro" // Captura qualquer outro erro de cálculo
    }
}

// --- PREVIEW ---
// Adicionei um Preview para você visualizar a tela de criação de níveis
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AdmScreenPreview() {
    // Use o mesmo tema do seu aplicativo para uma visualização precisa.
    // Se você usa "MyApplicationTheme", coloque-o aqui.
    MyApplicationTheme {
        // O Box com a cor de fundo ajuda a visualizar os componentes que são brancos.
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            AdmScreen()
        }
    }
}
