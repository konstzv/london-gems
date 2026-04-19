package com.londongemsapp.presentation.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.londongemsapp.domain.model.Category
import com.londongemsapp.domain.model.Recommendation
import com.londongemsapp.presentation.components.CategoryChip
import com.londongemsapp.presentation.components.EmptyState
import com.londongemsapp.presentation.components.RecommendationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onRecommendationClick: (String) -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categoryCounts by viewModel.categoryCounts.collectAsState()
    val lastSync by viewModel.lastSyncTimestamp.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("London Gems")
                        lastSync?.let { timestamp ->
                            val minutesAgo = (System.currentTimeMillis() - timestamp) / 60_000
                            Text(
                                text = when {
                                    minutesAgo < 1 -> "Last synced just now"
                                    minutesAgo == 1L -> "Last synced 1 min ago"
                                    else -> "Last synced $minutesAgo min ago"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search by title") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            CategoryChipRow(
                selectedCategory = selectedCategory,
                categoryCounts = categoryCounts,
                onCategorySelected = { viewModel.selectCategory(it) }
            )

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
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
                            EmptyState(
                                message = if (selectedCategory != null) {
                                    "No recommendations found for this category.\nTry a different filter or pull to refresh."
                                } else {
                                    "No recommendations yet.\nPull down to refresh."
                                },
                                icon = Icons.Outlined.Bookmark
                            )
                        } else {
                            RecommendationList(
                                recommendations = state.data,
                                onRecommendationClick = onRecommendationClick
                            )
                        }
                    }

                    is UiState.Error -> {
                        @Suppress("UNCHECKED_CAST")
                        val cached = state.cachedData as? List<Recommendation>
                        if (cached != null && cached.isNotEmpty()) {
                            Column {
                                Text(
                                    text = "Showing cached data. ${state.message}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                RecommendationList(
                                    recommendations = cached,
                                    onRecommendationClick = onRecommendationClick
                                )
                            }
                        } else {
                            EmptyState(
                                message = state.message,
                                icon = Icons.Outlined.ErrorOutline,
                                actionLabel = "Retry",
                                onAction = { viewModel.refresh() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChipRow(
    selectedCategory: Category?,
    categoryCounts: Map<Category, Int>,
    onCategorySelected: (Category?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = {
                    val total = categoryCounts.values.sum()
                    Text(if (total > 0) "All ($total)" else "All")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DoneAll,
                        contentDescription = "All"
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        items(Category.entries.toList()) { category ->
            CategoryChip(
                category = category,
                selected = category == selectedCategory,
                onSelected = onCategorySelected,
                count = categoryCounts[category]
            )
        }
    }
}

@Composable
private fun RecommendationList(
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
