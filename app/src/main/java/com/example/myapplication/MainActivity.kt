package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MenuSimples()
        }
    }
}

@Composable
fun MenuSimples() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        verticalArrangement = Arrangement.Top, // Garante que o conteúdo começa após o cabeçalho
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Cabeçalho com a seta
        TopBar()

        Spacer(modifier = Modifier.height(16.dp)) // Espaço entre o cabeçalho e o conteúdo

        // Primeira opção de menu com ícone fora do card
        Row(
            modifier = Modifier.fillMaxWidth(0.6f), // Row ocupa 60% da largura da tela
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Garante que o ícone fique à direita
        ) {
            OpçãoMenu(
                texto = "Igual a 10",
                cor = Color.Black
            )
            // Ícone de "Info" fora do card
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Ir para Como Jogar",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* Ação de navegação aqui, se necessário */ },
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espaço entre os botões

        // Segunda opção de menu com ícone fora do card
        Row(
            modifier = Modifier.fillMaxWidth(0.6f), // Row ocupa 60% da largura da tela
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Garante que o ícone fique à direita
        ) {
            OpçãoMenu(
                texto = "Valor de X",
                cor = Color.Black
            )
            // Ícone de "Info" fora do card
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Ir para Como Jogar",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {  },
                tint = Color.Black
            )
        }
    }
}

@Composable
fun OpçãoMenu(texto: String, cor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp)
            .clickable {  },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cor),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun TopBar() {
    // Cabeçalho fixo com a seta no topo
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botão de seta para a esquerda no canto superior esquerdo
        IconButton(
            onClick = { /* Ação de navegação, como voltar para a tela anterior */ },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuSimplesPreview() {
    MenuSimples()
}
