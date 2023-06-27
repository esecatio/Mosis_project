package com.example.mosis.screens

import BottomBarComponent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mosis.R
import com.example.mosis.data.FireStore.User
import com.example.mosis.navigation.MosisAppRouter
import com.example.mosis.navigation.Screen
import com.example.mosis.navigation.SystemBackButtonHandler
import com.example.mosis.ui.theme.Primary
import com.example.mosis.ui.theme.Secondary
import com.example.mosis.viewModel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    var firstName: String by remember { mutableStateOf("") }
    var lastName: String by remember { mutableStateOf("") }
    var image: String by remember { mutableStateOf("") }
    var totalScore: String by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    val editButtonLabel = if (isEditMode) "Save" else "Edit"

    val topUsersState = remember { mutableStateOf<List<User>>(emptyList()) }

    LaunchedEffect(Unit) {
        homeViewModel.getTopUsersFromFirestore { topUsers ->
            topUsersState.value = topUsers
        }
    }

    BottomBarComponent(currentRoute = MosisAppRouter.currentScreen.value)
    if (!isEditMode) {
        homeViewModel.getUserFromFirebase { data ->
            firstName = data.firstName
            lastName = data.lastName
            image = data.image
            totalScore = data.totalScore.toString()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isEditMode) {
            // Edit mode: display editable text fields
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.padding(0.dp, 10.dp)
            )

            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.padding(0.dp, 10.dp)
            )
        } else {
            // Display non-editable text
            Text(
                text = "$firstName $lastName",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Text(
            text = "Score: $totalScore",
            style = TextStyle(fontSize = 18.sp),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = {
                isEditMode = !isEditMode
                if (!isEditMode) {
                    // Save the edited name and last name
                    homeViewModel.updateUserInFirebase(firstName, lastName) {
                        // Update successful
                        isEditMode = false
                    }
                }
            },
            modifier = Modifier
                .width(300.dp)
                .heightIn(48.dp)
                .padding(20.dp),
            contentPadding = PaddingValues(),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(48.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(Secondary, Primary)),
                        shape = RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = editButtonLabel,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Primary,
                thickness = 1.dp
            )

            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(R.string.leader_board),
                fontSize = 18.sp,
                color = Primary
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Primary,
                thickness = 1.dp
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Top 5 Users",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn {
                itemsIndexed(topUsersState.value) { index, user ->
                    val itemColor = if (index == 0) Color(0xFFD4AF37) else Color.LightGray
                    Card(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = itemColor
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (index == 0) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Crown",
                                    tint = Color(0xFFFFD700), // Customize the crown color if needed
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Text(
                                text = "${index + 1}.",
                                modifier = Modifier.width(24.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "${user.firstName} ${user.lastName}",
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = user.totalScore.toString(),
                                modifier = Modifier.padding(start = 16.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                }
            }
        }
    }

    SystemBackButtonHandler {
        MosisAppRouter.navigateTo(Screen.HomeScreen)
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}