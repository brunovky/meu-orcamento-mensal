package com.brunooliveira.meuorcamentomensal.presentation.home

import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus
import com.brunooliveira.meuorcamentomensal.domain.usecase.ExpenseUseCases
import com.brunooliveira.meuorcamentomensal.notification.ExpenseNotifier
import com.brunooliveira.meuorcamentomensal.notification.NotificationPreferences
import com.brunooliveira.meuorcamentomensal.presentation.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var useCases: ExpenseUseCases
    private lateinit var notifier: ExpenseNotifier
    private lateinit var prefs: NotificationPreferences
    private lateinit var viewModel: ExpenseViewModel

    @Before
    fun setup() {
        useCases = mockk(relaxed = true)
        notifier = mockk(relaxed = true)
        prefs = mockk()

        coEvery { prefs.notificationHourFlow } returns flowOf(9)

        every { useCases.getAllExpenses() } returns flowOf(
            listOf(
                Expense(1, "Internet", 100.0, LocalDate.of(2025, 7, 10), PaymentStatus.PENDING),
                Expense(2, "Aluguel", 2000.0, LocalDate.of(2025, 7, 5), PaymentStatus.PAID),
                Expense(3, "Netflix", 50.0, LocalDate.of(2025, 8, 10), PaymentStatus.PENDING)
            )
        )

        viewModel = ExpenseViewModel(prefs, useCases, notifier)
    }

    @Test
    fun `updateSelectedMonthYear should filter expenses by month`() = runTest {
        assertThat(viewModel.selectedMonthYear.value).isEqualTo(YearMonth.now())

        val targetMonth = YearMonth.of(2025, 8)
        viewModel.updateSelectedMonthYear(targetMonth)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value

        assertThat(uiState.expenses).hasSize(1)
        assertThat(uiState.expenses.first().name).isEqualTo("Netflix")
    }

    @Test
    fun `deleteExpense should call useCase and cancel notification`() = runTest {
        val expense = Expense(1, "Internet", 100.0, LocalDate.now(), PaymentStatus.PENDING)

        coEvery { useCases.deleteExpense(expense) } just Runs
        coEvery { notifier.cancelNotification(1) } just Runs

        viewModel.deleteExpense(expense)

        advanceUntilIdle()

        coVerify { useCases.deleteExpense(expense) }
        coVerify { notifier.cancelNotification(1) }
    }

    @Test
    fun `insertExpense should schedule notification when expense is pending`() = runTest {
        val expense = Expense(0, "Internet", 100.0, LocalDate.of(2025, 8, 10), PaymentStatus.PENDING)

        coEvery { useCases.insertExpense(expense) } returns 10L
        coEvery { notifier.scheduleNotification(any(), any(), any(), any()) } just Runs

        viewModel.insertExpense(expense)

        advanceUntilIdle()

        coVerify { notifier.scheduleNotification(10, "Internet", LocalDate.of(2025, 8, 10), 9) }
    }

    @Test
    fun `insertExpense should cancel notification when expense is paid`() = runTest {
        val expense = Expense(0, "Internet", 100.0, LocalDate.of(2025, 8, 10), PaymentStatus.PAID)

        coEvery { useCases.insertExpense(expense) } returns 10L
        coEvery { notifier.cancelNotification(10) } just Runs

        viewModel.insertExpense(expense)

        advanceUntilIdle()

        coVerify { notifier.cancelNotification(10) }
    }

    @Test
    fun `copyExpensesToNextMonth should copy expenses with new dates and schedule notifications`() = runTest {
        val expenses = listOf(
            Expense(1, "Internet", 100.0, LocalDate.of(2025, 7, 10), PaymentStatus.PAID),
            Expense(2, "Aluguel", 2000.0, LocalDate.of(2025, 7, 5), PaymentStatus.PENDING)
        )

        coEvery { useCases.insertExpense(any()) } returnsMany listOf(10L, 11L)
        coEvery { notifier.scheduleNotification(any(), any(), any(), any()) } just Runs

        viewModel.copyExpensesToNextMonth(expenses)

        advanceUntilIdle()

        coVerify(exactly = 2) { useCases.insertExpense(any()) }
        coVerify(exactly = 1) { notifier.scheduleNotification(11, "Aluguel", LocalDate.of(2025, 8, 5), 9) }
    }

}