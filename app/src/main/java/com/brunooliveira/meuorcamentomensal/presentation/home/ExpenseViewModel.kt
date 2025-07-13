package com.brunooliveira.meuorcamentomensal.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus
import com.brunooliveira.meuorcamentomensal.domain.usecase.ExpenseUseCases
import com.brunooliveira.meuorcamentomensal.notification.NotificationPreferences
import com.brunooliveira.meuorcamentomensal.notification.cancelExpenseNotification
import com.brunooliveira.meuorcamentomensal.notification.scheduleExpenseNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

data class ExpenseUiState(
    val expenses: List<Expense> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val totalAmount: Double = 0.0,
    val totalPaid: Double = 0.0,
    val totalPending: Double = 0.0
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val notificationPreferences: NotificationPreferences,
    private val useCases: ExpenseUseCases,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState(isLoading = true))
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    private val _selectedMonthYear = MutableStateFlow(YearMonth.now())
    val selectedMonthYear: StateFlow<YearMonth> = _selectedMonthYear.asStateFlow()

    init {
        fetchExpenses()
    }

    private fun fetchExpenses() {
        useCases.getAllExpenses()
            .onStart {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
            .onEach { expenses ->
                val filtered = expenses.filter {
                    YearMonth.from(it.dueDate) == _selectedMonthYear.value
                }
                val total = filtered.sumOf { it.amount }
                val paid = filtered.filter { it.status == PaymentStatus.PAID }.sumOf { it.amount }
                val pending = total - paid
                _uiState.update {
                    it.copy(
                        expenses = filtered,
                        isLoading = false,
                        errorMessage = null,
                        totalAmount = total,
                        totalPaid = paid,
                        totalPending = pending
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun insertExpense(expense: Expense) = viewModelScope.launch {
        val generatedId = useCases.insertExpense(expense)
        if (expense.status == PaymentStatus.PAID) {
            cancelExpenseNotification(context, generatedId.toInt())
        } else {
            scheduleExpenseNotification(
                context = context,
                expenseId = generatedId.toInt(),
                expenseName = expense.name,
                dueDate = expense.dueDate,
                notificationHour = notificationPreferences.notificationHourFlow.first()
            )
        }
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        useCases.deleteExpense(expense)
        cancelExpenseNotification(context, expense.id)
    }

    fun updateSelectedMonthYear(newValue: YearMonth) {
        _selectedMonthYear.value = newValue
        fetchExpenses()
    }

    fun copyExpensesToNextMonth(expenses: List<Expense>) = viewModelScope.launch {
        expenses.forEach { expense ->
            val newExpense = expense.copy(
                id = 0, // Ou 0L dependendo do tipo
                dueDate = expense.dueDate.plusMonths(1),
                status = PaymentStatus.PENDING
            )
            val newId = useCases.insertExpense(newExpense)

            if (newExpense.status != PaymentStatus.PAID) {
                val notificationHour = notificationPreferences.notificationHourFlow.first()
                scheduleExpenseNotification(
                    context = context,
                    expenseId = newId.toInt(),
                    expenseName = newExpense.name,
                    dueDate = newExpense.dueDate,
                    notificationHour = notificationHour
                )
            }
        }
    }

}