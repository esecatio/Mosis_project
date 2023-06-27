package com.example.mosis.screens

import BottomBarComponent
import com.example.mosis.viewModel.ExerciseViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mosis.components.CategorySelectionScreen
import com.example.mosis.components.FlipCardList
import com.example.mosis.components.LevelSelectionScreen
import com.example.mosis.data.FireStore.Category
import com.example.mosis.data.FireStore.Exercise
import com.example.mosis.data.FireStore.Level
import com.example.mosis.navigation.MosisAppRouter
import com.example.mosis.navigation.Screen
import com.example.mosis.navigation.SystemBackButtonHandler
import com.example.mosis.viewModel.ExerciseUIEvent

@Composable
fun DoneScreen(exerciseViewModel: ExerciseViewModel = viewModel()) {
    val exercisesState = remember { mutableStateOf<List<Exercise>>(emptyList()) }
    val exercisesToShowState = remember { mutableStateOf(exercisesState.value) }
    val selectedLevelState = remember { mutableStateOf<Level?>(null) }
    val selectedCategoryState = remember { mutableStateOf<Category?>(null) }
    LaunchedEffect(Unit) {
        exerciseViewModel.getExercisesCompletedByCurrentUser(
            onSuccess = { exercises ->
                exercisesState.value = exercises
            }
        )
    }
    BottomBarComponent(currentRoute = MosisAppRouter.currentScreen.value)

    MosisAppRouter.getCategory()
    MosisAppRouter.getLevels()
    exercisesToShowState.value = exerciseViewModel.filterExercises(
        exercises = exercisesState.value,
        categoryFilter = selectedCategoryState.value?.categoryId,
        levelFilter = selectedLevelState.value?.levelId
    )

    Column() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            LevelSelectionScreen(
                levels = MosisAppRouter.listOfLevels.value,
                selectedLevel = selectedLevelState.value,
                onLevelSelected = { selectedLevel ->
                    selectedLevelState.value = selectedLevel
                    exerciseViewModel.setLevelFilter(selectedLevel.levelId)
                    exerciseViewModel.onEvent(ExerciseUIEvent.HardnessIdChanged(selectedLevel.levelId))
                    println("Selected Level: ${selectedLevel.levelId}")
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(10.dp)
        ) {
            CategorySelectionScreen(
                categories = MosisAppRouter.listOfCategories.value,
                selectedCategory = selectedCategoryState.value,
                onCategorySelected = { selectedCategory ->
                    selectedCategoryState.value = selectedCategory
                    exerciseViewModel.setCategoryFilter(selectedCategory.categoryId)

                    println("Selected Level: ${selectedCategory.categoryId}")
                }
            )
        }

        FlipCardList(exercisesToShowState.value)
    }
    SystemBackButtonHandler {
        MosisAppRouter.navigateTo(Screen.HomeScreen)
    }
}

@Preview
@Composable
fun DoneScreenPreview() {
    DoneScreen()
}