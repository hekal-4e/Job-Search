    package com.example.jobsearchapp.ui

    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Archive
    import androidx.compose.material.icons.filled.Description
    import androidx.compose.material.icons.filled.Home
    import androidx.compose.material.icons.filled.Person
    import androidx.compose.material.icons.filled.Star
    import androidx.compose.material3.Icon
    import androidx.compose.material3.NavigationBar
    import androidx.compose.material3.NavigationBarItem
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.getValue
    import androidx.navigation.NavController
    import androidx.navigation.compose.currentBackStackEntryAsState

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val items = listOf(
            BottomNavItem("Home", Icons.Default.Home, "home"),
            BottomNavItem("Saved", Icons.Default.Star, "savedJobs"),
            BottomNavItem("Applications", Icons.Default.Description, "myApplications"),
            // it named "Proposals"
            BottomNavItem("Post Job", Icons.Default.Archive, "poster"),
                    BottomNavItem("Profile", Icons.Default.Person, "profile")


        )

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }

    data class BottomNavItem(
        val title: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val route: String
    )
