package com.brunooliveira.meuorcamentomensal.domain.usecase

import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus
import com.brunooliveira.meuorcamentomensal.domain.repository.ExpenseRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CopyExpensesUseCaseTest {

    private lateinit var repository: ExpenseRepository
    private lateinit var useCase: CopyExpensesUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = CopyExpensesUseCase(repository)
    }

    @Test
    fun `should copy expenses to next month with updated due dates`() = runTest {
        val expenses = listOf(
            Expense(1, "Internet", 150.0, LocalDate.of(2025, 7, 10), PaymentStatus.PENDING),
            Expense(2, "Aluguel", 2000.0, LocalDate.of(2025, 7, 5), PaymentStatus.PAID)
        )
        val insertedExpenses = slot<List<Expense>>()

        coEvery { repository.insertAll(capture(insertedExpenses)) } returns listOf(1L, 2L)

        // Act
        useCase(expenses, 2025, 8)

        coVerify(exactly = 1) { repository.insertAll(any()) }

        val copiedExpenses = insertedExpenses.captured

        assertThat(copiedExpenses).hasSize(2)

        val copiedLastExpense = copiedExpenses.last()

        assertThat(copiedLastExpense.name).isEqualTo("Aluguel") // O último capturado será "Aluguel"
        assertThat(copiedLastExpense.dueDate.monthValue).isEqualTo(8)
        assertThat(copiedLastExpense.dueDate.dayOfMonth).isEqualTo(5)
        assertThat(copiedLastExpense.amount).isEqualTo(2000.0)
        assertThat(copiedLastExpense.status).isEqualTo(PaymentStatus.PENDING)
    }

    @Test
    fun `should not insert anything when expense list is empty`() = runTest {
        // Arrange
        val expenses = emptyList<Expense>()

        // Act
        useCase(expenses, 2025, 8)

        // Assert
        coVerify(exactly = 0) { repository.insertAll(any()) }
    }

}