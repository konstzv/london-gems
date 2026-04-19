package com.londongemsapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.londongemsapp.presentation.detail.DetailScreen
import com.londongemsapp.presentation.favorites.FavoritesScreen
import com.londongemsapp.presentation.feed.FeedScreen
import com.londongemsapp.presentation.settings.SettingsScreen

private data class TopLevelRoute(
    val label: String,
    val route: Route,
    val icon: ImageVector
)

private val topLevelRoutes = listOf(
    TopLevelRoute("Feed", Route.Feed, Icons.Filled.Explore),
    TopLevelRoute("Favorites", Route.Favorites, Icons.Filled.FavoriteBorder),
    TopLevelRoute("Settings", Route.Settings, Icons.Filled.Settings)
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Hide bottom bar on Detail screen
    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        topLevelRoutes.any { route -> dest.hasRoute(route.route::class) }
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelRoutes.forEach { topLevelRoute ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = topLevelRoute.icon,
                                    contentDescription = topLevelRoute.label
                                )
                            },
                            label = { Text(topLevelRoute.label) },
                            selected = currentDestination?.hierarchy?.any { dest ->
                                dest.hasRoute(topLevelRoute.route::class)
                            } == true,
                            onClick = {
                                navController.navigate(topLevelRoute.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Feed,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Route.Feed> {
                FeedScreen(
                    onRecommendationClick = { placeId ->
                        navController.navigate(Route.Detail(placeId))
                    }
                )
            }

            composable<Route.Favorites> {
                FavoritesScreen(
                    onRecommendationClick = { placeId ->
                        navController.navigate(Route.Detail(placeId))
                    }
                )
            }

            composable<Route.Settings> {
                SettingsScreen()
            }

            composable<Route.Detail> { backStackEntry ->
                val detail: Route.Detail = backStackEntry.toRoute()
                DetailScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
