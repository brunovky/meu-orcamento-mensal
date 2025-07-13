package com.brunooliveira.meuorcamentomensal.presentation.add

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunooliveira.meuorcamentomensal.BR_DATE_FORMAT
import com.brunooliveira.meuorcamentomensal.COMMA
import com.brunooliveira.meuorcamentomensal.DOT
import com.brunooliveira.meuorcamentomensal.EMPTY
import com.brunooliveira.meuorcamentomensal.R
import com.brunooliveira.meuorcamentomensal.REGEX_DIGITS
import com.brunooliveira.meuorcamentomensal.REGEX_NUMBERS_COMMA
import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus
import com.brunooliveira.meuorcamentomensal.domain.model.toDisplayString
import com.brunooliveira.meuorcamentomensal.presentation.common.CurrencyVisualTransformation
import com.brunooliveira.meuorcamentomensal.presentation.home.ExpenseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: ExpenseViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(EMPTY) }
    var amount by remember { mutableStateOf(EMPTY) }
    var dueDate by remember { mutableStateOf(LocalDate.now()) }
    var status by remember { mutableStateOf(PaymentStatus.PENDING) }
    var expanded by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }
    var valueError by remember { mutableStateOf(false) }

    // DatePickerDialog para selecionar data
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                dueDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            dueDate.year,
            dueDate.monthValue - 1,
            dueDate.dayOfMonth
        )
    }

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_expense)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                            R.string.back
                        ), tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text(stringResource(R.string.expense_name)) },
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { input ->
                        // Permite apenas números e vírgula
                        if (input.matches(Regex(REGEX_NUMBERS_COMMA))) {
                            amount = input
                            valueError = false
                        }
                    },
                    label = { Text(stringResource(R.string.amount)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = CurrencyVisualTransformation(),
                    isError = valueError,
                    modifier = Modifier.fillMaxWidth()
                )

                DateSelectorField(dueDate = dueDate) {
                    datePickerDialog.show()
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = status.toDisplayString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.status_label)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        PaymentStatus.entries.forEach {
                            DropdownMenuItem(
                                text = { Text(it.toDisplayString()) },
                                onClick = {
                                    status = it
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        nameError = name.isBlank()
                        valueError = amount.isBlank() || amount.replace(REGEX_DIGITS.toRegex(), EMPTY).toLongOrNull()?.let { it <= 0 } ?: true

                        if (!nameError && !valueError) {
                            val amountDouble = amount.replace(COMMA, DOT).toDoubleOrNull() ?: 0.0
                            viewModel.insertExpense(
                                Expense(
                                    id = 0,
                                    name = name,
                                    amount = amountDouble,
                                    dueDate = dueDate,
                                    status = status
                                )
                            )

                            Toast.makeText(context,
                                context.getString(R.string.save_expense_successfully), Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    )
}

@Composable
fun DateSelectorField(
    dueDate: LocalDate,
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern(BR_DATE_FORMAT)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        OutlinedTextField(
            value = dueDate.format(formatter),
            onValueChange = {},
            label = { Text(stringResource(R.string.due_date_label)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = stringResource(R.string.select_date)
                )
            },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledTrailingIconColor = Color.Gray,
                disabledLabelColor = Color.Gray,
                disabledBorderColor = Color.Gray
            )
        )
    }
}