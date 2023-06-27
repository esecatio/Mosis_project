import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mosis.data.FireStore.Exercise
import com.example.mosis.navigation.MosisAppRouter
import com.example.mosis.navigation.Screen
import com.example.mosis.navigation.SystemBackButtonHandler
import com.example.mosis.viewModel.ExerciseViewModel
import kotlinx.coroutines.delay

@SuppressLint("AutoboxingStateValueProperty")
@Composable
fun ExerciseScreen(exercise: Exercise, exerciseViewModel: ExerciseViewModel = viewModel()) {
    var levelTime: Int by remember { mutableIntStateOf(0) }
    val remaining = remember {
        mutableIntStateOf(0)
    }
    var timerSet: Boolean by remember {
        mutableStateOf(false)
    }
    val userAnswerState = remember { mutableStateOf("") }
    var isAnswerSelected: Boolean by remember {
        mutableStateOf(false)
    }
    var answerFieldColor: Color by remember {
        mutableStateOf(Color.Gray)
    }
    var levelScore: Int by remember { mutableIntStateOf(0) }
    exerciseViewModel.getLevelFromFirebase(exercise.hardnessId) { l ->
        levelTime = l.time
        if (!timerSet) {
            remaining.value = l.time
            timerSet = true
        }
    }
    exerciseViewModel.getLevelFromFirebase(exercise.hardnessId) { l ->
        levelScore = l.points
    }


    if (timerSet && !isAnswerSelected)
        LaunchedEffect(Unit)
        {
            while (remaining.value > 0) {
                delay(1000)
                remaining.value--
            }
        }

    if (remaining.value == 0 && timerSet) {
        isAnswerSelected = true
        answerFieldColor = Color.Blue
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = remaining.value.toString(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Gray,
            style = TextStyle(fontSize = 90.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = exercise.text,
            style = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            exercise.fakeAnswer1.let { answer ->
                AnswerOption(
                    text = answer,
                    isSelected = userAnswerState.value == answer,
                    color = answerFieldColor,
                    onSelected = {
                        if (!isAnswerSelected) {
                            isAnswerSelected = true
                            userAnswerState.value = answer
                            answerFieldColor = Color.Red
                            exerciseViewModel.updateUserScoreInFirebase((-1) * levelScore) {
                            }
                        }
                    }
                )
            }

            exercise.fakeAnswer2.let { answer ->
                AnswerOption(
                    text = answer,
                    isSelected = userAnswerState.value == answer,
                    color = answerFieldColor,
                    onSelected = {
                        if (!isAnswerSelected) {
                            isAnswerSelected = true
                            userAnswerState.value = answer
                            answerFieldColor = Color.Red
                            exerciseViewModel.updateUserScoreInFirebase((-1) * levelScore) {
                            }
                        }
                    }
                )
            }

            exercise.fakeAnswer3.let { answer ->
                AnswerOption(
                    text = answer,
                    isSelected = userAnswerState.value == answer,
                    color = answerFieldColor,
                    onSelected = {
                        if (!isAnswerSelected) {
                            isAnswerSelected = true
                            userAnswerState.value = answer
                            answerFieldColor = Color.Red
                            exerciseViewModel.updateUserScoreInFirebase((-1) * levelScore) {
                            }
                        }
                    }
                )
            }

            exercise.correctAnswer.let { answer ->
                AnswerOption(
                    text = answer,
                    isSelected = userAnswerState.value == answer,
                    color = answerFieldColor,
                    onSelected = {
                        if (!isAnswerSelected) {
                            isAnswerSelected = true
                            userAnswerState.value = answer
                            answerFieldColor = Color.Green
                            exerciseViewModel.updateUserScoreInFirebase(levelScore) {
                            }
                            exerciseViewModel.addUserExerciseConnection(exercise.latitude.toString() + exercise.longitude.toString())
                        }
                    }
                )
            }
        }
    }
    SystemBackButtonHandler {
        MosisAppRouter.navigateTo(Screen.MapScreen)
    }
}

@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    color: Color,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelected() }
                .padding(16.dp)
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}