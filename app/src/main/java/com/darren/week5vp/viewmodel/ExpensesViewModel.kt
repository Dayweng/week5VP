package com.darren.week5vp.viewmodel

import androidx.lifecycle.ViewModel
import com.darren.week5vp.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ExpensesViewModel : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _total = MutableStateFlow(0L)
    val total: StateFlow<Long> = _total

    private var nextId = 0

    fun validateAmount(text: String): String? {
        val amount = text.toLongOrNull()
        return if
                (amount == null || amount <= 0) "Amount must be greater than 0"
        else
            null
    }

    fun addExpense(name: String, amountText: String): Boolean {
        if (name.isBlank() || validateAmount(amountText) != null) return false

        val newExpense = Expense(nextId++, name.trim(), amountText.toLong())
        _expenses.value = _expenses.value + newExpense
        updateTotal()
        return true
    }

    fun deleteExpense(expense: Expense) {
        _expenses.value = _expenses.value.filter { it.id != expense.id }
        updateTotal()
    }

    //aft every add delete function to update the top nubmer total
    private fun updateTotal() {
        _total.value = _expenses.value.sumOf { it.amount }
    }

    fun editExpense(id: Int, name: String, amountText: String): Boolean {
        if (name.isBlank() || validateAmount(amountText) != null) return false

        _expenses.value = _expenses.value.map {
            if (it.id == id) it.copy(name = name.trim(), amount = amountText.toLong()) else it
        }
        updateTotal()
        return true
    }
}