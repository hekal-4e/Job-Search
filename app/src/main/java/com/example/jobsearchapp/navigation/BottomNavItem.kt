package com.example.jobsearchapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(
        route = Screen.Home.route,
        title = "Home",
        icon = Icons.Default.Home
    )

    data object Search : BottomNavItem(
        route = "search",
        title = "Search",
        icon = Icons.Default.Search
    )

    data object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )

    data object SavedJobs : BottomNavItem(
        route = "SavedJobs",
        title = "SavedJobs",
        icon = Icons.Default.SavedSearch
    )

    companion object {
        val items = listOf(Home, Search, Profile, SavedJobs)
    }
}