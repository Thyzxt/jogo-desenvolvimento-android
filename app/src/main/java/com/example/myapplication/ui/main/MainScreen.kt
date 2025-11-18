package com.example.myapplication.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.common.MyViewModelFactory
import com.example.myapplication.ui.screen_jogo.telaJogo
import com.example.myapplication.ui.screen_jogo.JogoViewModel
import com.example.myapplication.ui.screen_comojogar.ComoJogarScreen
import com.example.myapplication.ui.screen_ranking.RankingScreen
import com.example.myapplication.ui.screen_ranking.RankingViewModel


@Composable
fun MainScreen(
    factory: MyViewModelFactory,
    mainNavController: NavHostController // O controlador de navegação principal (para Admin/Logout)
) {
    // Este é o NavController *interno* para a BottomNavBar
    val nestedNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = nestedNavController)
        }
    ) { innerPadding ->
        // Este NavHost aninhado é o miolo app
        // Ele troca o conteúdo principal (Jogo, Ranking, ComoJogar)
        NavHost(
            navController = nestedNavController,
            startDestination = BottomNavItem.Jogo.route, // Começa na tela de Jogo
            modifier = Modifier.padding(innerPadding)
        ) {
            // Rota para a Tela de Jogo
            composable(BottomNavItem.Jogo.route) {
                val gameViewModel: JogoViewModel = viewModel(factory = factory)
                telaJogo(
                    viewModel = gameViewModel,
                    onNavigateBack = {
                        // O botão de voltar no jogo agora faz o logout
                        mainNavController.navigate("login") { popUpTo(0) }
                    }
                )
            }

            // Rota para a Tela de Ranking
            composable(BottomNavItem.Ranking.route) {
                val rankingViewModel: RankingViewModel = viewModel(factory = factory)
                RankingScreen(
                    viewModel = rankingViewModel,
                    onNavigateBack = { nestedNavController.popBackStack() }
                )
            }

            // Rota para a Tela "Como Jogar"
            composable(BottomNavItem.ComoJogar.route) {
                ComoJogarScreen(
                    onNavigateBack = { nestedNavController.popBackStack() }
                )
            }
        }
    }
}

/**
 * Este é o componente da Barra de Navegação no rodapé
 */
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Jogo,
        BottomNavItem.Ranking,
        BottomNavItem.ComoJogar
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Sobe para o destino inicial do gráfico para evitar construir uma pilha grande de destinos na back stack enquanto o usuário seleciona itens
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Evita múltiplas cópias do mesmo destino ao selecionar o mesmo item novamente
                        launchSingleTop = true
                        // Restaura o estado ao selecionar um item previamente selecionado
                        restoreState = true
                    }
                }
            )
        }
    }
}