package com.example.myapplication.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.repository.GameRepository
import com.example.myapplication.ui.screen_jogo.JogoViewModel
import com.example.myapplication.ui.screen_login.LoginViewModel
import com.example.myapplication.ui.screen_cadastro.CadastroViewModel
import com.example.myapplication.ui.screen_adm.AdmViewModel
import com.example.myapplication.ui.screen_ranking.RankingViewModel
class MyViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(JogoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JogoViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(CadastroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CadastroViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(AdmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdmViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(RankingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RankingViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}