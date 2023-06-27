package com.example.mosis.screens

import com.example.mosis.viewModel.ExerciseViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mosis.R
import com.example.mosis.components.ButtonComponent
import com.example.mosis.components.CategorySelectionScreen
import com.example.mosis.components.HeadingTextComponent
import com.example.mosis.components.LevelSelectionScreen
import com.example.mosis.components.SimpleTextFieldComponent
import com.example.mosis.components.TextAreaComponent
import com.example.mosis.data.FireStore.Category
import com.example.mosis.data.FireStore.Level
import com.example.mosis.navigation.MosisAppRouter
import com.example.mosis.viewModel.ExerciseUIEvent

@Composable
fun NewExerciseForm(
    latitude: Double,
    longitude: Double,
    exerciseViewModel: ExerciseViewModel = viewModel()
) {
    exerciseViewModel.onEvent(ExerciseUIEvent.LongitudeChanged(longitude))
    exerciseViewModel.onEvent(ExerciseUIEvent.LatitudeChanged(latitude))
    val selectedLevelState = remember { mutableStateOf<Level?>(null) }
    val selectedCategoryState = remember { mutableStateOf<Category?>(null) }
    MosisAppRouter.getLevels()
    MosisAppRouter.getCategory()
    Box(
        contentAlignment = Alignment.Center
    ) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(28.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())) {

                HeadingTextComponent(value = stringResource(R.string.create_new_exercise))

                TextAreaComponent(
                    labelValue = "Exercise Text",
                    onTextChanged = {
                        exerciseViewModel.onEvent(ExerciseUIEvent.TextChanged(it))
                    }
                )
                //Select1
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    LevelSelectionScreen(
                        levels = MosisAppRouter.listOfLevels.value,
                        selectedLevel = selectedLevelState.value,
                        onLevelSelected = { selectedLevel ->
                            selectedLevelState.value = selectedLevel
                            exerciseViewModel.onEvent(
                                ExerciseUIEvent.HardnessIdChanged(
                                    selectedLevel.levelId
                                )
                            )
                            println("Selected Level: ${selectedLevel.levelId}")
                        }
                    )
                }
                //Select2
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    CategorySelectionScreen(
                        categories = MosisAppRouter.listOfCategories.value,
                        selectedCategory = selectedCategoryState.value,
                        onCategorySelected = { selectedCategory ->
                            selectedCategoryState.value = selectedCategory
                            exerciseViewModel.onEvent(
                                ExerciseUIEvent.CategoryIdChanged(
                                    selectedCategory.categoryId
                                )
                            )
                            println("Selected Level: ${selectedCategory.categoryId}")
                        }
                    )
                }

                Row {
                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 5.dp)) {
                        SimpleTextFieldComponent(
                            labelValue = "Correct Answer",
                            onTextChanged = { text ->
                                exerciseViewModel.onEvent(ExerciseUIEvent.CorrectAnswerChanged(text))
                            }
                        )
                    }

                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 5.dp)) {
                        SimpleTextFieldComponent(
                            labelValue = "Wrong Answer 1",
                            onTextChanged = { text ->
                                exerciseViewModel.onEvent(ExerciseUIEvent.FakeAnswer1Changed(text))
                            }
                        )
                    }
                }

                Row {
                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)) {
                        SimpleTextFieldComponent(
                            labelValue = "Wrong Answer 2",
                            onTextChanged = { text ->
                                exerciseViewModel.onEvent(ExerciseUIEvent.FakeAnswer2Changed(text))
                            }
                        )
                    }

                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)) {
                        SimpleTextFieldComponent(
                            labelValue = "Wrong Answer 3",
                            onTextChanged = { text ->
                                exerciseViewModel.onEvent(ExerciseUIEvent.FakeAnswer3Changed(text))
                            }
                        )
                    }
                }
                ButtonComponent(
                    value = "Save",
                    onButtonClicked = {
                        exerciseViewModel.onEvent(ExerciseUIEvent.SaveButtonClicked)
                    },
                    isEnabled = true
                )
            }
        }
    }
}