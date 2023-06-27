package com.example.mosis.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mosis.data.FireStore.User
import com.example.mosis.navigation.MosisAppRouter
import com.example.mosis.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class HomeViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signOut()

        val authStateListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                MosisAppRouter.navigateTo(Screen.LoginScreen)
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    fun getUserFromFirebase(data: (User) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val user = it.toObject<User>()!!
                        data(user)
                    }
                }
                .addOnFailureListener { error ->
                    Log.d("Mosis", "Failed to create user in Firestore: ${error.message}")
                }
        }
    }

    fun updateUserInFirebase(firstName: String, lastName: String, onSuccess: () -> Unit) {
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.uid?.let { userId ->
            val userRef = firestore.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val existingUser = document.toObject(User::class.java)
                        existingUser?.let { user ->
                            // Update the specific fields you want to change
                            user.firstName = firstName
                            user.lastName = lastName

                            // Save the updated user object back to Firestore
                            userRef.set(user)
                                .addOnSuccessListener {
                                    onSuccess()
                                }
                                .addOnFailureListener { error ->
                                    Log.d(
                                        "Mosis",
                                        "Failed to update user in Firestore: ${error.message}"
                                    )
                                }
                        }
                    } else {
                        Log.d("Mosis", "User document not found in Firestore")
                    }
                }
                .addOnFailureListener { error ->
                    Log.d("Mosis", "Failed to fetch user document from Firestore: ${error.message}")
                }
        }
    }

    //Fetch All Done Exercise

    //Fetch All Created Exercise

    //Top 5 users
    fun getTopUsersFromFirestore(onSuccess: (List<User>) -> Unit) {
        firestore.collection("users")
            .orderBy("totalScore", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val topUsers = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(User::class.java)
                }
                onSuccess(topUsers)
            }
            .addOnFailureListener { error ->
                Log.d("Mosis", "Failed to fetch top users from Firestore: ${error.message}")
            }
    }
}