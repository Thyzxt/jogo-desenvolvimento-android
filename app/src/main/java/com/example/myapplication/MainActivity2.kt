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
import androidx.compose.ui.text.font.FontWeight

class MainActivity2 : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent{
            TelaLogin()
        }
    }
}

@Composable
fun TelaLogin(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        TopBarLogin()

        Spacer(modifier = Modifier.height(135.dp))

        Text(
            text = "MODOS DE JOGO",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(135.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){

            OpcaoMenu(
                texto = "IGUAL A 10",
                cor = Color.Black
            )

            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Ir para Como Jogar",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {  },
                tint = Color.Black
            )}

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){

            OpcaoMenu(
                texto = "VALOR DE X",
                cor = Color.Black
            )

            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Ir para Como Jogar",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {  },
                tint = Color.Black
            )
        }
    }
}

@Composable
fun Login(texto: String, cor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(18.dp)
            .clickable {  },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cor),
        elevation = CardDefaults.cardElevation(6.dp)
    ){
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = texto,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun TopBarLogin(){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ){

        IconButton(
            onClick = {  },
            modifier = Modifier.size(40.dp)
        ){
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
fun LoginPreview() {
    TelaLogin()
}
