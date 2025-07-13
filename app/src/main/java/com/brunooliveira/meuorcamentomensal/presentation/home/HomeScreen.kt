package com.brunooliveira.meuorcamentomensal.presentation.home

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunooliveira.meuorcamentomensal.BR_DATE_FORMAT
import com.brunooliveira.meuorcamentomensal.DATE_FORMAT_MONTH_YEAR
import com.brunooliveira.meuorcamentomensal.R
import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus
import com.brunooliveira.meuorcamentomensal.domain.model.toDisplayString
import com.brunooliveira.meuorcamentomensal.ui.theme.FabColor
import com.brunooliveira.meuorcamentomensal.ui.theme.PrimaryColor
import java.text.NumberFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ExpenseViewModel = hiltViewModel(),
    onAddExpenseClick: () -> Unit,
    onEditExpenseClick: (Int) -> Unit,
    onSettingsClick: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    val selectedMonthYear by viewModel.selectedMonthYear.collectAsState()
    val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_MONTH_YEAR, Locale.getDefault())
    var showMonthPicker by remember { mutableStateOf(false) }

    // Estados para modo de seleção
    val isSelectionMode = remember { mutableStateOf(false) }
    val selectedExpenses = remember { mutableStateListOf<Expense>() }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSelectionMode.value) {
                        Text(stringResource(R.string.selected_expenses, selectedExpenses.size))
                    } else {
                        Text(stringResource(R.string.monthly_budget))
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = PrimaryColor,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    if (isSelectionMode.value) {
                        IconButton(onClick = {
                            isSelectionMode.value = false
                            selectedExpenses.clear()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                                R.string.cancel_selection
                            ))
                        }
                    }
                },
                actions = {
                    if (!isSelectionMode.value) {
                        IconButton(onClick = { showMonthPicker = true }) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = stringResource(R.string.select_month)
                            )
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            viewModel.copyExpensesToNextMonth(selectedExpenses.toList())
                            isSelectionMode.value = false
                            selectedExpenses.clear()
                            Toast.makeText(context,
                                context.getString(R.string.copy_expenses_successfully), Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.copy_to_next_month))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isSelectionMode.value) {
                FloatingActionButton(
                    onClick = onAddExpenseClick,
                    containerColor = FabColor,
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_expense))
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            Text(
                text = selectedMonthYear.format(formatter).replaceFirstChar { it.uppercase() },
                modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null) {
                Text(
                    text = stringResource(R.string.fetch_error, uiState.errorMessage),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                MonthlySummaryCard(
                    total = uiState.totalAmount,
                    paid = uiState.totalPaid,
                    pending = uiState.totalPending
                )

                if (uiState.expenses.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.placeholder_expense))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        items(uiState.expenses, key = { it.id }) { expense ->
                            val isSelected = selectedExpenses.contains(expense)

                            if (isSelectionMode.value) {
                                ExpenseCardSelectable(
                                    expense = expense,
                                    isSelected = isSelected,
                                    onClick = {
                                        if (isSelected) selectedExpenses.remove(expense)
                                        else selectedExpenses.add(expense)
                                    }
                                )
                            } else {
                                ExpenseItemSwipeToDelete(
                                    expense = expense,
                                    onDeleteConfirmed = { viewModel.deleteExpense(it) }
                                ) {
                                    ExpenseCard(
                                        expense = expense,
                                        onItemClick = { onEditExpenseClick(expense.id) },
                                        onLongClick = {
                                            isSelectionMode.value = true
                                            selectedExpenses.add(expense)
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        if (showMonthPicker) {
            AlertDialog(
                onDismissRequest = { showMonthPicker = false },
                title = { Text(stringResource(R.string.select_month_title)) },
                text = {
                    Column {
                        val months = (-6..6).map { YearMonth.now().minusMonths(it.toLong()) }
                        months.forEach { ym ->
                            Text(
                                text = ym.format(formatter).replaceFirstChar { it.uppercase() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        viewModel.updateSelectedMonthYear(ym)
                                        showMonthPicker = false
                                    }
                            )
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}

@Composable
fun MonthlySummaryCard(
    total: Double,
    paid: Double,
    pending: Double
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.monthly_summary), style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            SummaryRow(stringResource(R.string.total), formatter.format(total), MaterialTheme.colorScheme.onPrimaryContainer)
            SummaryRow(stringResource(R.string.paid), formatter.format(paid), Color(0xFF2E7D32))
            SummaryRow(stringResource(R.string.pending), formatter.format(pending), Color(0xFFE65100))
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun SummaryRow(label: String, value: String, color: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, color = color, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseCard(
    expense: Expense,
    onItemClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val formatterCurrency = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val formatterDate = DateTimeFormatter.ofPattern(BR_DATE_FORMAT)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onItemClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier
                .fillMaxHeight()
                .weight(1F)) {
                Text(text = expense.name, style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.due_date, expense.dueDate.format(formatterDate)),
                    style = MaterialTheme.typography.bodyMedium
                )

                val statusText = expense.status.toDisplayString()
                val statusColor = if (expense.status == PaymentStatus.PAID)
                    Color(0xFF2E7D32) else Color(0xFFD84315)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.status, statusText),
                    color = statusColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterVertically)) {
                Text(text = formatterCurrency.format(expense.amount), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseItemSwipeToDelete(
    expense: Expense,
    onDeleteConfirmed: (Expense) -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { distance -> distance * 0.5f },
        confirmValueChange = { dismissValue ->
            if (dismissValue != SwipeToDismissBoxValue.Settled) {
                showDialog = true
            }
            false // não remove automaticamente
        }
    )


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.confirm_delete)) },
            text = { Text(stringResource(R.string.confirm_delete_question, expense.name)) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDeleteConfirmed(expense)
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.error)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.left_delete),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.right_delete),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        },
        content = content
    )
}

@Composable
fun ExpenseCardSelectable(
    expense: Expense,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val formatterCurrency = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val formatterDate = DateTimeFormatter.ofPattern(BR_DATE_FORMAT)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.surface
        })
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() }
            )

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1F)) {
                Text(text = expense.name, style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.due_date, expense.dueDate.format(formatterDate)),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(text = formatterCurrency.format(expense.amount), fontWeight = FontWeight.Bold)
        }
    }
}
