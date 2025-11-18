package com.example.myapplication.ui.screen_ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.entities.RankingEntity
import com.example.myapplication.data.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class RankingViewModel(private val repository: GameRepository) : ViewModel() {


    // A UI vai coletar isso e se atualizará automaticamente.
    val ranking: StateFlow<List<RankingEntity>> = repository.getRanking()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList() // Começa com uma lista vazia
        )
}