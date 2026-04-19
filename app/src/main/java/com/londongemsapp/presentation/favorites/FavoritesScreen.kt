package com.londongemsapp.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.londongemsapp.domain.model.Recommendation
import com.londongemsapp.presentation.components.EmptyState
import com.londongemsapp.presentation.components.RecommendationCard
import com.londongemsapp.presentation.feed.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onRecommendationClick: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filter by viewModel.filter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Segmented button row
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                FavoritesFilter.entries.forEachIndexed { index, filterOption ->
                    SegmentedButton(
                        selected = filter == filterOption,
                        onClick = { viewModel.setFilter(filterOption) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = FavoritesFilter.entries.size
                        )
                    ) {
                        Text(
                            text = when (filterOption) {
                                FavoritesFilter.ALL -> "All"
                                FavoritesFilter.DONE -> "Done"
                                FavoritesFilter.NOT_DONE -> "Not Done"
                            }
                        )
                    }
                }
            }

            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        val emptyMessage = when (filter) {
                            FavoritesFilter.ALL ->
                                "No saved recommendations yet.\nBrowse the feed and tap the bookmark icon to save threads."
                            FavoritesFilter.DONE ->
                                "No completed recommendations.\nMark recommendations as done to see them here."
                            FavoritesFilter.NOT_DONE ->
                                "All saved recommendations are done!"
                        }
                        EmptyState(
                            message = emptyMessage,
                            icon = Icons.Outlined.FavoriteBorder
                        )
                    } else {
                        FavoritesList(
                            recommendations = state.data,
                            onRecommendationClick = onRecommendationClick
                        )
                    }
                }

                is UiState.Error -> {
                    EmptyState(message = state.message)
                }
            }
        }
    }
}

@Composable
private fun FavoritesList(
    recommendations: List<Recommendation>,
    onRecommendationClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = recommendations,
            key = { it.redditId }
        ) { recommendation ->
            RecommendationCard(
                recommendation = recommendation,
                onClick = { onRecommendationClick(recommendation.redditId) }
            )
        }
    }
}
