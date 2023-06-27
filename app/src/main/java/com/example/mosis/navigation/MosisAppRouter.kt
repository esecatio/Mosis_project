package com.example.mosis.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.mosis.data.FireStore.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore

sealed class Screen {

    object SignUpScreen : Screen()
    object LoginScreen : Screen()
    object HomeScreen : Screen()
    object MapScreen : Screen()
    object NewExerciseForm : Screen()
    object ExerciseScreen : Screen()
    object DoneScreen : Screen()
    object CreatedExercisesScreen : Screen()
}

object MosisAppRouter {
    var latLng: LatLng = LatLng(0.0, 0.0);
    var currentScreen: MutableState<Screen> = mutableStateOf(Screen.SignUpScreen)
    var listOfLevels: MutableState<List<Level>> = mutableStateOf(emptyList())
    var listOfCategories: MutableState<List<Category>> = mutableStateOf(emptyList())
    var exercise: MutableState<Exercise> = mutableStateOf(Exercise())

    @SuppressLint("StaticFieldLeak")
    val firestore = FirebaseFirestore.getInstance()
    fun getLevels() {
        firestore.collection("levels").get().addOnSuccessListener { querySnapshot ->
                val levels = querySnapshot.documents.map { document ->
                    val levelId = document.id
                    val name = document.getString("name") ?: ""
                    val points = document.getLong("points")?.toInt() ?: 0
                    val time = document.getLong("time")?.toInt() ?: 0
                    Level(levelId, name, points, time)
                }
                listOfLevels.value = levels
            }.addOnFailureListener { _ ->
            }
    }

    fun getCategory() {
        firestore.collection("cathegories").get().addOnSuccessListener { querySnapshot ->
                val categories = querySnapshot.documents.map { document ->
                    val categoryId = document.id
                    val name = document.getString("name") ?: ""
                    Category(categoryId, name)
                }
                listOfCategories.value = categories
            }.addOnFailureListener { _ ->
            }
    }

    fun navigateTo(destination: Screen) {
        currentScreen.value = destination
    }

}