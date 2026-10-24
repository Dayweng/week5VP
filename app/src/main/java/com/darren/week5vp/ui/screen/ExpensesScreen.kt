package com.darren.week5vp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darren.week5vp.viewmodel.ExpensesViewModel
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.darren.week5vp.model.Expense
import com.darren.week5vp.ui.theme.Week5vpTheme
import com.darren.week5vp.util.formatRupiah

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = viewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val total by viewModel.total.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }

    var editingExpense by remember { mutableStateOf<Expense?>(null) }
    var editName by remember { mutableStateOf("") }
    var editAmount by remember { mutableStateOf("") }

    val editError = if (editAmount.isEmpty()) null else viewModel.validateAmount(editAmount)

    val amountError = if (amountText.isEmpty()) null else viewModel.validateAmount(amountText)
    val canAdd = name.isNotBlank() && amountText.isNotEmpty() && amountError == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.width(80.dp))
        Column {
            Text("Expenses", fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Text("Total Expense : Rp ${formatRupiah(total)}", fontWeight = FontWeight.Medium)
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Expense Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = amountText,
                //simple Error handling
                onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                label = { Text("Amount of Expense") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = amountError != null,
                supportingText = { if (amountError != null) Text(amountError) },
                modifier = Modifier.weight(1f)
            )

            FilledTonalIconButton(
                onClick = {
                    if (viewModel.addExpense(name, amountText)) {
                        name = ""
                        amountText = ""
                    }
                },
                enabled = canAdd,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(expenses, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Name : ${item.name}", fontWeight = FontWeight.Bold)
                            Text("Rp ${formatRupiah(item.amount)}")
                        }
                        Row(){
                            IconButton(
                                onClick = {
                                    //filled in boxes with current value
                                    editName = item.name
                                    editAmount = item.amount.toString()
                                    editingExpense = item
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }

                            IconButton(onClick ={ viewModel.deleteExpense(item) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    //edit module ui
    editingExpense?.let { expense ->
        AlertDialog(
            onDismissRequest = { editingExpense = null },
            title = { Text("Edit Expense") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Expense Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editAmount,
                        onValueChange = { editAmount = it.filter { c -> c.isDigit() } },
                        label = { Text("Amount of Expense") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = editError != null,
                        supportingText = { if (editError != null) Text(editError) }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // close the dialog only if the edit worked
                        if (viewModel.editExpense(expense.id, editName, editAmount)) {
                            editingExpense = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingExpense = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExpensesScreenPreview() {
    Week5vpTheme {
        ExpensesScreen()
    }
}
