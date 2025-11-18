package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.GameRepository
import com.example.myapplication.ui.common.MyViewModelFactory
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.screen_login.LoginViewModel
import com.example.myapplication.ui.screen_login.TelaLogin
import com.example.myapplication.ui.screen_cadastro.CadastroViewModel
import com.example.myapplication.ui.screen_cadastro.TelaCad
import com.example.myapplication.ui.screen_adm.AdmScreen
import com.example.myapplication.ui.screen_adm.AdmViewModel
import com.example.myapplication.ui.main.MainScreen // Tela principal com Bottom Nav
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// As importações das rotas aninhadas (Jogo, Ranking, ComoJogar)
// foram removidas deste arquivo, pois elas só são usadas DENTRO da MainScreen.kt.

class MainActivity : ComponentActivity() {

    //Injeção de Dependência Manual
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val firestore by lazy { Firebase.firestore }
    private val firebaseAuth by lazy { Firebase.auth }

    private val repository by lazy { GameRepository(database.rankingDao(), firestore, firebaseAuth) }

    private val viewModelFactory by lazy { MyViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                // O NAVHOST PRINCIPAL SÓ TEM 4 ROTAS (Login, Cadastro, Main, Admin)
                NavHost(navController = navController, startDestination = "login") {

                    // 1. Rota Login
                    composable("login") {
                        val loginViewModel: LoginViewModel = viewModel(factory = viewModelFactory)
                        TelaLogin(
                            viewModel = loginViewModel,
                            onNavigateToRegister = {
                                navController.navigate("register")
                            },
                            onNavigateToMenu = {
                                // ROTA DE SUCESSO AGORA VAI PARA 'main'
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToAdmin = {
                                navController.navigate("admin") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 2. Rota Cadastro
                    composable("register") {
                        val viewModel: CadastroViewModel = viewModel(factory = viewModelFactory)
                        TelaCad(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // 3. ROTA PRINCIPAL (HOST DO BOTTOM NAV BAR)
                    composable("main") {
                        MainScreen(
                            factory = viewModelFactory, // Passamos a Factory para MainScreen instanciar os ViewModels internos
                            mainNavController = navController // Passamos o NavController principal
                        )
                    }

                    // 4. ROTA ADMIN
                    composable("admin") {
                        val viewModel: AdmViewModel = viewModel(factory = viewModelFactory)
                        AdmScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // AS ROTAS "JOGO", "RANKING" E "COMOJOGAR" SUMIRAM DAQUI
                    // ELAS ESTÃO DENTRO DO NAVHOST ANINHADO NA MAINSCREEN
                }
            }
        }
    }
}