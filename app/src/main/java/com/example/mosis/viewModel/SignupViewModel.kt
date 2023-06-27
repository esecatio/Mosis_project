package com.example.mosis.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mosis.data.FireStore.User
import com.example.mosis.data.RegistrationUIState
import com.example.mosis.navigation.MosisAppRouter
import com.example.mosis.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupViewModel : ViewModel() {
    var registrationUIState = mutableStateOf(RegistrationUIState())

    var allValidationsPassed = mutableStateOf(false)

    var signUpInProgress = mutableStateOf(false)

    fun onEvent(event: SignupUIEvent) {
        when (event) {
            is SignupUIEvent.FirstNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    firstName = event.firstName
                )
            }

            is SignupUIEvent.LastNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    lastName = event.lastName
                )
            }

            is SignupUIEvent.EmailChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    email = event.email
                )

            }

            is SignupUIEvent.PasswordChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    password = event.password
                )

            }

            is SignupUIEvent.RegisterButtonClicked -> {
                signUp()
            }
        }
        validateDataWithRules()
    }


    private fun signUp() {
        createUserInFirebase(
            User(
                email = registrationUIState.value.email,
                firstName = registrationUIState.value.firstName,
                lastName = registrationUIState.value.lastName,
                password = registrationUIState.value.password,
                image = registrationUIState.value.image,
                totalScore = 0
            )
        )
    }

    private fun validateDataWithRules() {
        val fNameResult = Validator.validateFirstName(
            fName = registrationUIState.value.firstName
        )

        val lNameResult = Validator.validateLastName(
            lName = registrationUIState.value.lastName
        )

        val emailResult = Validator.validateEmail(
            email = registrationUIState.value.email
        )


        val passwordResult = Validator.validatePassword(
            password = registrationUIState.value.password
        )

        val privacyPolicyResult = Validator.validatePrivacyPolicyAcceptance(
            statusValue = registrationUIState.value.privacyPolicyAccepted
        )

        registrationUIState.value = registrationUIState.value.copy(
            firstNameError = fNameResult.status,
            lastNameError = lNameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status,
            privacyPolicyError = privacyPolicyResult.status
        )

        allValidationsPassed.value = fNameResult.status && lNameResult.status &&
                emailResult.status && passwordResult.status && privacyPolicyResult.status

    }

//User to DataBase and Auth
    private val firestore = FirebaseFirestore.getInstance()

    private fun createUserInFirebase(user: User) {
        signUpInProgress.value = true

        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { task ->
                signUpInProgress.value = false
                if (task.isSuccessful) {
                    auth.currentUser?.uid?.let { userId ->
                        firestore.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                MosisAppRouter.navigateTo(Screen.HomeScreen)
                            }
                            .addOnFailureListener { error ->
                                Log.d("Mosis","Failed to create user in Firestore: ${error.message}")
                            }
                    }
                }
            }
    }
}