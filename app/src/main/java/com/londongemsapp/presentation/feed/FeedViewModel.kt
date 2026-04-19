package com.londongemsapp.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.londongemsapp.domain.model.Category
import com.londongemsapp.domain.model.DataResult
import com.londongemsapp.domain.model.Recommendation
import com.londongemsapp.domain.repository.RecommendationRepository
import com.londongemsapp.domain.usecase.GetRecommendationsUseCase
import com.londongemsapp.domain.usecase.SyncRecommendationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getRecommendations: GetRecommendationsUseCase,
    private val syncRecommendations: SyncRecommendationsUseCase,
    private val repository: RecommendationRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _initialLoadComplete = MutableStateFlow(false)
    private val _syncError = MutableStateFlow<String?>(null)

    val categoryCounts: StateFlow<Map<Category, Int>> = getRecommendations(null)
        .map { recommendations ->
            recommendations.groupBy { it.category }.mapValues { it.value.size }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    val lastSyncTimestamp: StateFlow<Long?> = repository.getLastSyncTimestamp()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val uiState: StateFlow<UiState<List<Recommendation>>> = combine(
        _selectedCategory.flatMapLatest { category -> getRecommendations(category) },
        _initialLoadComplete,
        _syncError
    ) { recommendations, loadComplete, syncError ->
        when {
            recommendations.isNotEmpty() -> UiState.Success(recommendations)
            !loadComplete -> UiState.Loading
            syncError != null -> UiState.Error(syncError)
            else -> UiState.Success(recommendations)
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    init {
        refresh()
    }

    fun selectCategory(category: Category?) {
        _selectedCategory.value = category
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _syncError.value = null
            try {
                val result = syncRecommendations()
                if (result is DataResult.Error) {
                    _syncError.value = result.exception.message ?: "Sync failed"
                }
            } catch (e: Exception) {
                _syncError.value = e.message ?: "Sync failed"
            } finally {
                _isRefreshing.value = false
                _initialLoadComplete.value = true
            }
        }
    }
}
