package com.example.mosis.data.FireStore

import com.google.firebase.auth.FirebaseUser
data class Exercise(
    val text:String="",
    val hardnessId:String="",
    val categoryId:String="",
    val correctAnswer:String="",
    val fakeAnswer1:String="",
    val fakeAnswer2:String="",
    val fakeAnswer3:String="",
    val creatorId: String="",
    val latitude: Double=0.0,
    val longitude: Double=0.0
)