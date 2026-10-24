package com.darren.week5vp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darren.week5vp.viewmodel.QuizViewModel

@Composable
fun QuizApp(
    viewModel: QuizViewModel = viewModel()
) {
    val isFinished by viewModel.isFinished.collectAsState()

    if (isFinished) {
        ResultScreen(viewModel)
    } else {
        QuizScreen(viewModel)
    }
}

@Composable
fun QuizScreen(
    viewModel: QuizViewModel
) {
    val questions by viewModel.quizQuestions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val selectedAnswer by viewModel.selectedAnswer.collectAsState()
    val question = questions[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Text("Question ${currentIndex + 1}", fontSize = 20.sp)
        Text(question.text)

        Spacer(modifier = Modifier.height(8.dp))

        question.options.forEach { option ->
            val isSelected = option == selectedAnswer
            Button(
                onClick = { viewModel.selectAnswer(option) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(option)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.next() },
            enabled = selectedAnswer != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next")
        }
    }
}

@Composable
fun ResultScreen(
    viewModel: QuizViewModel
) {
    val questions by viewModel.quizQuestions.collectAsState()
    val score by viewModel.score.collectAsState()
    val wrongAnswers by viewModel.wrongAnswers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text("Quiz Finished!", fontSize = 24.sp)
        Text("Your Score: $score / ${questions.size}")

        if (wrongAnswers.isEmpty()) {
            Text("Perfect! No wrong answers.", color = MaterialTheme.colorScheme.primary)
        } else {
            wrongAnswers.forEach { item ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Q${item.number}: ${item.question}", fontSize = 14.sp)
                    Text("Your Answer: ${item.yourAnswer}", color = Color.Red, fontSize = 12.sp)
                    Text(
                        "Correct Answer: ${item.correctAnswer}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        Button(
            onClick = { viewModel.restart() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restart Quiz")
        }
    }
}