package com.londongemsapp.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Feed : Route

    @Serializable
    data object Favorites : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data class Detail(val placeId: String) : Route
}
