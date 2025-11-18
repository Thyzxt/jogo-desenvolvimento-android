package com.example.myapplication.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home // Ícone para Jogo
import androidx.compose.material.icons.filled.List // Ícone para Ranking
import androidx.compose.material.icons.filled.Info // Ícone para Como Jogar
import androidx.compose.ui.graphics.vector.ImageVector

// Define a estrutura de cada item na barra de navegação
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Jogo : BottomNavItem(
        route = "jogo",
        title = "Jogo",
        icon = Icons.Default.Home
    )
    object Ranking : BottomNavItem(
        route = "ranking",
        title = "Ranking",
        icon = Icons.Default.List
    )
    object ComoJogar : BottomNavItem(
        route = "como_jogar", // Rota diferente de "comoJogar"
        title = "Ajuda",
        icon = Icons.Default.Info
    )
}