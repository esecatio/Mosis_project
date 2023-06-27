package com.example.mosis.data.FireStore

data class User(
    val email:String="",
    var firstName:String="",
    var lastName:String="",
    val password:String="",
    val image:String="",
    var totalScore:Int=0
    )