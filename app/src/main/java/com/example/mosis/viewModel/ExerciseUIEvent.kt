package com.example.mosis.viewModel

sealed class ExerciseUIEvent {

    data class TextChanged(val text: String) : ExerciseUIEvent()
    data class HardnessIdChanged(val hardnessId: String) : ExerciseUIEvent()
    data class CategoryIdChanged(val categoryId: String) : ExerciseUIEvent()
    data class CorrectAnswerChanged(val correctAnswer: String) : ExerciseUIEvent()
    data class FakeAnswer1Changed(val fakeAnswer1: String) : ExerciseUIEvent()
    data class FakeAnswer2Changed(val fakeAnswer2: String) : ExerciseUIEvent()
    data class FakeAnswer3Changed(val fakeAnswer3: String) : ExerciseUIEvent()
    data class LatitudeChanged(val latitude: Double) : ExerciseUIEvent()
    data class LongitudeChanged(val longitude: Double) : ExerciseUIEvent()

    object SaveButtonClicked : ExerciseUIEvent()
}