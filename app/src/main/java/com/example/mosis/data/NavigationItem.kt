package com.example.mosis.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mosis.navigation.Screen

sealed class NavigationItem(
    val route: Screen,
    val title: String,
    val icon: ImageVector
) {
    object Home : NavigationItem(
        route = Screen.HomeScreen,
        title = "Profile",
        icon = Icons.Default.Home
    )

    object Done : NavigationItem(
        route = Screen.DoneScreen,
        title = "Done Exercises",
        icon = Icons.Default.Done
    )

    object Created : NavigationItem(
        route = Screen.CreatedExercisesScreen,
        title = "Created Exercises",
        icon = Icons.Default.Create
    )

    object Map : NavigationItem(
        route = Screen.MapScreen,
        title = "Map",
        icon = Icons.Default.Map
    )
}