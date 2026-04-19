package com.londongemsapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.londongemsapp.domain.model.Recommendation
import com.londongemsapp.domain.repository.RecommendationRepository
import com.londongemsapp.domain.usecase.ToggleDoneUseCase
import com.londongemsapp.domain.usecase.ToggleFavoriteUseCase
import com.londongemsapp.presentation.feed.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: RecommendationRepository,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val toggleDone: ToggleDoneUseCase
) : ViewModel() {

    private val placeId: String = checkNotNull(savedStateHandle["placeId"])

    val uiState: StateFlow<UiState<Recommendation>> = repository
        .getRecommendation(placeId)
        .map { recommendation ->
            if (recommendation != null) {
                UiState.Success(recommendation)
            } else {
                UiState.Error("Recommendation not found")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun toggleFavorite() {
        viewModelScope.launch {
            toggleFavorite(placeId)
        }
    }

    fun toggleDone() {
        viewModelScope.launch {
            toggleDone(placeId)
        }
    }
}
