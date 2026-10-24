package com.darren.week5vp.viewmodel

import androidx.lifecycle.ViewModel
import com.darren.practicew3_4.model.Question
import com.darren.practicew3_4.model.WrongAnswer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuizViewModel : ViewModel() {

    // the 5 question pool
    private val allQuestions = listOf(
        Question("Ibu Kota Di Indonesia?", listOf("Surabaya", "Bandung", "Jakarta", "Medan"), "Jakarta"),
        Question("Apakah Mata Kuliah VP susah?", listOf("Sangat Mudah", "Mudah", "Sulit", "Sangat Sulit"), "Sangat Mudah"),
        Question("Natal dirayakan pada bulan?", listOf("Agustus", "Oktober", "Desember", "Februari"), "Desember"),
        Question("Bahasa pemrograman untuk Android?", listOf("Swift", "Kotlin", "Ruby", "PHP"), "Kotlin"),
        Question("Berapa hasil 7 x 6?", listOf("36", "42", "48", "54"), "42")
    )

    //3 questions randomized
    private val _quizQuestions = MutableStateFlow(allQuestions.shuffled().take(3))
    val quizQuestions: StateFlow<List<Question>> = _quizQuestions

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _selectedAnswer = MutableStateFlow<String?>(null)
    val selectedAnswer: StateFlow<String?> = _selectedAnswer

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _wrongAnswers = MutableStateFlow<List<WrongAnswer>>(emptyList())
    val wrongAnswers: StateFlow<List<WrongAnswer>> = _wrongAnswers

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished

    fun selectAnswer(answer: String) {
        _selectedAnswer.value = answer
    }

    fun next() {
        val answer = _selectedAnswer.value ?: return
        val question = _quizQuestions.value[_currentIndex.value]

        //answer checking
        if (answer == question.correctAnswer) {
            _score.value += 1
        } else {
            val wrong = WrongAnswer(_currentIndex.value + 1, question.text, answer, question.correctAnswer)
            _wrongAnswers.value = _wrongAnswers.value + wrong
        }

        if (_currentIndex.value == _quizQuestions.value.size - 1) {
            _isFinished.value = true
        } else {
            _currentIndex.value += 1
            _selectedAnswer.value = null
        }
    }

    fun restart() {
        _quizQuestions.value = allQuestions.shuffled().take(3)
        _currentIndex.value = 0
        _selectedAnswer.value = null
        _score.value = 0
        _wrongAnswers.value = emptyList()
        _isFinished.value = false
    }
}