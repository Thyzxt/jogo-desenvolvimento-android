package com.example.myapplication.ui.screen_comojogar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComoJogarScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C)),
        topBar = {
            TopAppBar(title = { Text("Como Jogar") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Este jogo desafia você a formar exatamente o número 10 usando quatro números fornecidos em cada nível. " +
                        "É um puzzle rápido, lógico e progressivo, onde cada nível traz uma combinação diferente para você descobrir como chegar ao 10.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÃO PARA USAR O CALLBACK
            OutlinedButton(
                onClick = onNavigateBack, // Chama o callback
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text("Voltar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ComoJogarScreenPreview() {
    MyApplicationTheme {
        //PREVIEW ATUALIZADO
        ComoJogarScreen(onNavigateBack = {})
    }
}