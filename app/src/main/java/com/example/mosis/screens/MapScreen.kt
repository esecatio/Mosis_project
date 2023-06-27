import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mosis.R
import com.example.mosis.components.RadiusSlider
import com.example.mosis.data.FireStore.Exercise
import com.example.mosis.data.MapUIState
import com.example.mosis.navigation.MosisAppRouter
import com.example.mosis.navigation.Screen
import com.example.mosis.navigation.SystemBackButtonHandler
import com.example.mosis.viewModel.ExerciseViewModel
import com.example.mosis.viewModel.MapViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("PotentialBehaviorOverride", "AutoboxingStateValueProperty")
@Composable
fun MapScreen(
    state: MapUIState,
    mapViewModel: MapViewModel = viewModel(),
    exercisesViewModel: ExerciseViewModel = viewModel()
) {
    val mapProperties = MapProperties(
        isMyLocationEnabled = state.lastKnownLocation != null,
    )
    val radius = remember { mutableFloatStateOf(1000f) }
    val createdByUser: MutableState<List<Exercise>> = remember { mutableStateOf(emptyList()) }
    val createdByUserToShow = remember { mutableStateOf(createdByUser.value) }

    val completedByUser: MutableState<List<Exercise>> = remember { mutableStateOf(emptyList()) }
    val completedByUserToShow = remember { mutableStateOf(completedByUser.value) }

    val clickableExercise: MutableState<List<Exercise>> = remember { mutableStateOf(emptyList()) }
    val clickableExerciseToShow = remember { mutableStateOf(clickableExercise.value) }

    LaunchedEffect(Unit) {
        exercisesViewModel.getExercisesCreatedByCurrentUser { createdByUserExercises ->
            createdByUser.value = createdByUserExercises
        }
    }

    LaunchedEffect(Unit) {
        exercisesViewModel.getExercisesCompletedByCurrentUser { completedByUserExercises ->
            completedByUser.value = completedByUserExercises
        }
    }

    LaunchedEffect(Unit) {
        exercisesViewModel.getExercisesNotCreatedByCurrentUser { allExercises ->
            clickableExercise.value = allExercises
        }
    }

    if (state.lastKnownLocation != null) {
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(
                    state.lastKnownLocation.latitude, state.lastKnownLocation.longitude
                ), 10f
            )
        }

        val filteredExercisesCreated = mutableListOf<Exercise>()
        for (exercise in createdByUser.value) {
            val distance = mapViewModel.calculateDistance(
                state.lastKnownLocation.latitude,
                state.lastKnownLocation.longitude,
                exercise.latitude,
                exercise.longitude
            )
            if (distance <= radius.value) {
                filteredExercisesCreated.add(exercise)
            }
        }
        createdByUserToShow.value = filteredExercisesCreated


        val filteredExercisesCompleted = mutableListOf<Exercise>()
        for (exercise in completedByUser.value) {
            val distance = mapViewModel.calculateDistance(
                state.lastKnownLocation.latitude,
                state.lastKnownLocation.longitude,
                exercise.latitude,
                exercise.longitude
            )
            if (distance <= radius.value) {
                filteredExercisesCompleted.add(exercise)
            }
        }
        completedByUserToShow.value = filteredExercisesCompleted

        val filteredExercisesClickable = mutableListOf<Exercise>()
        for (exercise in clickableExercise.value) {
            val distance = mapViewModel.calculateDistance(
                state.lastKnownLocation.latitude,
                state.lastKnownLocation.longitude,
                exercise.latitude,
                exercise.longitude
            )
            if (distance <= radius.value) {
                filteredExercisesClickable.add(exercise)
            }
        }
        clickableExerciseToShow.value = filteredExercisesClickable

        Box(modifier = Modifier.fillMaxSize()) {
            BottomBarComponent(currentRoute = MosisAppRouter.currentScreen.value)
            GoogleMap(properties = mapProperties,
                cameraPositionState = cameraPositionState,
                onMapClick = { ll ->
                    MosisAppRouter.latLng = ll
                    MosisAppRouter.navigateTo(Screen.NewExerciseForm)
                }) {
                Marker(
                    state = MarkerState(
                        position = LatLng(
                            state.lastKnownLocation.latitude, state.lastKnownLocation.longitude
                        )
                    ), title = "Current Location", snippet = "Marker for Your Location"
                )

                for (exercise in createdByUserToShow.value) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                exercise.latitude, exercise.longitude
                            )
                        )
                    )
                }

                for (exercise in completedByUserToShow.value) {
                    Marker(state = MarkerState(
                        position = LatLng(
                            exercise.latitude, exercise.longitude
                        )
                    ),
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.location_done),
                        onClick = { true })
                }

                for (exercise in clickableExerciseToShow.value) {
                    Marker(state = MarkerState(
                        position = LatLng(
                            exercise.latitude, exercise.longitude
                        )
                    ),
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.location_clickable),
                        onClick = {
                            MosisAppRouter.exercise.value = exercise
                            MosisAppRouter.navigateTo(Screen.ExerciseScreen)
                            true
                        })
                }

            }
            RadiusSlider(value = radius.value, onValueChange = { r ->
                radius.value = r
            })

        }

        SystemBackButtonHandler {
            MosisAppRouter.navigateTo(Screen.HomeScreen)
        }
    }
}