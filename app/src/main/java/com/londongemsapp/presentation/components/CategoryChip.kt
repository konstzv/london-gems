package com.londongemsapp.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.londongemsapp.domain.model.Category

@Composable
fun CategoryChip(
    category: Category,
    selected: Boolean,
    onSelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = { onSelected(category) },
        label = { Text(category.displayName()) },
        leadingIcon = {
            Icon(
                imageVector = category.icon(),
                contentDescription = category.displayName(),
                modifier = Modifier
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}

fun Category.displayName(): String = when (this) {
    Category.FOOD_AND_DRINKS -> "Food & Drinks"
    Category.EVENTS -> "Events"
    Category.PARKS_AND_NATURE -> "Parks & Nature"
    Category.CULTURE_AND_MUSEUMS -> "Culture & Museums"
    Category.NIGHTLIFE -> "Nightlife"
    Category.HIDDEN_GEMS -> "Hidden Gems"
    Category.UNCATEGORIZED -> "Other"
}

fun Category.icon(): ImageVector = when (this) {
    Category.FOOD_AND_DRINKS -> Icons.Filled.LocalDining
    Category.EVENTS -> Icons.Filled.Event
    Category.PARKS_AND_NATURE -> Icons.Filled.EmojiNature
    Category.CULTURE_AND_MUSEUMS -> Icons.Filled.Museum
    Category.NIGHTLIFE -> Icons.Filled.Nightlife
    Category.HIDDEN_GEMS -> Icons.Filled.TravelExplore
    Category.UNCATEGORIZED -> Icons.Filled.Category
}
