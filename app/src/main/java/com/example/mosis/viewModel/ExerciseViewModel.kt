package com.example.mosis.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mosis.data.FireStore.Exercise
import com.example.mosis.data.FireStore.Level
import com.example.mosis.data.FireStore.User
import com.example.mosis.data.FireStore.UserExercise
import com.example.mosis.navigation.MosisAppRouter
import com.example.mosis.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ExerciseViewModel : ViewModel() {
    private var exerciseUIState = mutableStateOf(Exercise())

    fun onEvent(event: ExerciseUIEvent) {
        when (event) {
            is ExerciseUIEvent.TextChanged -> {
                exerciseUIState.value = exerciseUIState.value.copy(
                    text = event.text
                )
            }

            is ExerciseUIEvent.HardnessIdChanged -> {
                exerciseUIState.value = exerciseUIState.value.copy(
                    hardnessId = event.hardnessId
                )
            }

            is ExerciseUIEvent.CategoryIdChanged -> {
                exerciseUIState.value = exerciseUIState.value.copy(
                    categoryId = event.categoryId
                )
            }

            is ExerciseUIEvent.CorrectAnswerChanged -> {
                exerciseUIState.value = exerciseUIState.value.copy(
                    correctAnswer = event.correctAnswer
                )
            }

            is ExerciseUIEvent.FakeAnswer1Changed -> {
                exerciseUIState.value = exerciseUIState.value.copy(
                    fakeAnswer1 = event.fakeAnswer1
                )
            }

            is ExerciseUIEvent.FakeAnswer2Changed -> {
                exerciseUIState.value = exerciseUIState.value.copy(
                    fakeAnswer2 = event.fakeAnswer2
                )
            }

            is ExerciseUIEvent.FakeAnswer3Changed -> {
                exerciseUIState.value = exerciseUIState.value.copy(
                    fakeAnswer3 = event.fakeAnswer3
                )
            }

            is ExerciseUIEvent.LatitudeChanged -> {
                exerciseUIState.value = exerciseUIState.value.copy(
                    latitude = event.latitude
                )
            }

            is ExerciseUIEvent.LongitudeChanged -> {
                exerciseUIState.value = exerciseUIState.value.copy(
                    longitude = event.longitude
                )
            }

            is ExerciseUIEvent.SaveButtonClicked -> {
                save()
            }
        }
    }

    private fun save() {
        val auth = FirebaseAuth.getInstance()
        createExerciseinFireBase(
            Exercise(
                exerciseUIState.value.text,
                exerciseUIState.value.hardnessId,
                exerciseUIState.value.categoryId,
                exerciseUIState.value.correctAnswer,
                exerciseUIState.value.fakeAnswer1,
                exerciseUIState.value.fakeAnswer2,
                exerciseUIState.value.fakeAnswer3,
                auth.currentUser!!.uid,
                exerciseUIState.value.latitude,
                exerciseUIState.value.longitude
            )
        )
    }

    private val firestore = FirebaseFirestore.getInstance()

    private fun createExerciseinFireBase(e: Exercise) {
        firestore.collection("exercises").document(e.latitude.toString() + e.longitude.toString())
            .set(e).addOnSuccessListener {
                MosisAppRouter.navigateTo(Screen.MapScreen)
            }.addOnFailureListener { error ->
                Log.d("Mosis", "Failed to create exercise in Firestore: ${error.message}")
            }
    }

    fun getLevelFromFirebase(levelId: String, data: (Level) -> Unit) {
        firestore.collection("levels").document(levelId).get().addOnSuccessListener {
            if (it.exists()) {
                val level = it.toObject<Level>()!!
                data(level)
            }
        }.addOnFailureListener { error ->
            Log.d("Mosis", "Failed to get level from Firestore: ${error.message}")
        }
    }

    fun updateUserScoreInFirebase(score: Int, onSuccess: () -> Unit) {
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    if (user != null) {
                        val currentTotalScore = user.totalScore
                        val updatedTotalScore = currentTotalScore + score
                        user.totalScore = updatedTotalScore
                        firestore.collection("users").document(userId).set(user)
                            .addOnSuccessListener {
                                onSuccess()
                            }.addOnFailureListener { error ->
                                Log.d(
                                    "Mosis", "Failed to update user in Firestore: ${error.message}"
                                )
                            }
                    } else {
                        Log.d("Mosis", "User object is null")
                    }
                }.addOnFailureListener { error ->
                    Log.d("Mosis", "Failed to fetch user from Firestore: ${error.message}")
                }
        }
    }

    fun addUserExerciseConnection(exerciseId: String) {
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.uid?.let { userId ->
            val uE = UserExercise(userId, exerciseId, "")
            firestore.collection("done").document(userId + exerciseId).set(uE)
        }
    }

    @SuppressLint("MutableCollectionMutableState")
    fun getExercisesCompletedByCurrentUser(
        onSuccess: (List<Exercise>) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val e = mutableStateOf(mutableListOf<Exercise>())
        auth.currentUser?.uid?.let { currentUserUid ->
            firestore.collection("done").whereEqualTo("user", currentUserUid).get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.mapNotNull { document ->
                        val exerciseId = document.getString("exercise")
                        if (exerciseId != null) {
                            firestore.collection("exercises").document(exerciseId).get()
                                .addOnSuccessListener { exerciseDocument ->
                                    val exercise = exerciseDocument.toObject(Exercise::class.java)
                                    exercise?.let {
                                        e.value.add(it)
                                    }
                                    onSuccess(e.value)
                                }
                        }
                    }
                    onSuccess(e.value)
                }.addOnFailureListener {}
        }
    }

    fun getExercisesCreatedByCurrentUser(
        onSuccess: (List<Exercise>) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.uid?.let { currentUserUid ->
            firestore.collection("exercises").whereEqualTo("creatorId", currentUserUid).get()
                .addOnSuccessListener { querySnapshot ->
                    val exercises = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(Exercise::class.java)
                    }
                    onSuccess(exercises)
                }.addOnFailureListener {}
        }
    }

    fun getExercisesNotCreatedByCurrentUser(
        onSuccess: (List<Exercise>) -> Unit
    ) {
        getExercisesCompletedByCurrentUser { completedExercises ->
            getExercisesCreatedByCurrentUser { createdExercises ->
                firestore.collection("exercises").get().addOnSuccessListener { querySnapshot ->
                    val allExercises = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(Exercise::class.java)
                    }

                    val notCreatedByCurrentUser = allExercises.filter { exercise ->
                        exercise !in completedExercises && exercise !in createdExercises
                    }

                    onSuccess(notCreatedByCurrentUser)
                }.addOnFailureListener {}
            }
        }
    }

    private val _categoryFilter = mutableStateOf("")

    private val _levelFilter = mutableStateOf("")

    fun setCategoryFilter(category: String) {
        _categoryFilter.value = category
    }

    fun setLevelFilter(level: String) {
        _levelFilter.value = level
    }

    fun filterExercises(
        exercises: List<Exercise>, categoryFilter: String?, levelFilter: String?
    ): List<Exercise> {
        if (categoryFilter != null && levelFilter != null) return exercises.filter { exercise ->
            exercise.categoryId == categoryFilter && exercise.hardnessId == levelFilter
        }
        else if (categoryFilter != null) return exercises.filter { exercise ->
            exercise.categoryId == categoryFilter
        }
        else if (levelFilter != null) return exercises.filter { exercise ->
            exercise.hardnessId == levelFilter
        }
        else return exercises
    }
}