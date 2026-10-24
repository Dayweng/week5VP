package com.darren.practicew3_4.model

data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswer: String
)

data class WrongAnswer(
    val number: Int,
    val question: String,
    val yourAnswer: String,
    val correctAnswer: String
)
