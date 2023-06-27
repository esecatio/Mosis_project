package com.example.mosis.app

import ExerciseScreen
import MapScreen
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import com.example.mosis.data.MapUIState
import com.example.mosis.navigation.MosisAppRouter
import com.example.mosis.navigation.Screen
import com.example.mosis.screens.CreatedExercisesScreen
import com.example.mosis.screens.DoneScreen
import com.example.mosis.screens.HomeScreen
import com.example.mosis.screens.LoginScreen
import com.example.mosis.screens.NewExerciseForm
import com.example.mosis.screens.SignUpScreen

@Composable
fun MosisApp(
    state: MapUIState
) {
    Crossfade(targetState = MosisAppRouter.currentScreen) { currentState ->
        when (currentState.value) {
            is Screen.SignUpScreen -> {
                SignUpScreen()
            }

            is Screen.LoginScreen -> {
                LoginScreen()
            }

            is Screen.HomeScreen -> {
                HomeScreen()
            }

            is Screen.MapScreen -> {
                MapScreen(state)
            }

            is Screen.DoneScreen -> {
                DoneScreen()
            }

            is Screen.NewExerciseForm -> {
                NewExerciseForm(
                    latitude = MosisAppRouter.latLng.latitude,
                    longitude = MosisAppRouter.latLng.longitude
                )
            }

            is Screen.ExerciseScreen -> {
                ExerciseScreen(exercise = MosisAppRouter.exercise.value)
            }

            is Screen.CreatedExercisesScreen -> {
                CreatedExercisesScreen()
            }
        }
    }

}
