package com.londongemsapp.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.londongemsapp.domain.model.Recommendation
import com.londongemsapp.domain.repository.RecommendationRepository
import com.londongemsapp.domain.usecase.ToggleFavoriteUseCase
import com.londongemsapp.presentation.feed.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FavoritesFilter {
    ALL, DONE, NOT_DONE
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: RecommendationRepository,
    private val toggleFavorite: ToggleFavoriteUseCase
) : ViewModel() {

    private val _filter = MutableStateFlow(FavoritesFilter.ALL)
    val filter: StateFlow<FavoritesFilter> = _filter.asStateFlow()

    val uiState: StateFlow<UiState<List<Recommendation>>> = combine(
        repository.getFavorites(),
        _filter
    ) { favorites, filter ->
        val filtered = when (filter) {
            FavoritesFilter.ALL -> favorites
            FavoritesFilter.DONE -> favorites.filter { it.isDone }
            FavoritesFilter.NOT_DONE -> favorites.filter { !it.isDone }
        }
        UiState.Success(filtered) as UiState<List<Recommendation>>
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    fun setFilter(filter: FavoritesFilter) {
        _filter.value = filter
    }

    fun toggleFavorite(recommendationId: String) {
        viewModelScope.launch {
            toggleFavorite.invoke(recommendationId)
        }
    }
}
