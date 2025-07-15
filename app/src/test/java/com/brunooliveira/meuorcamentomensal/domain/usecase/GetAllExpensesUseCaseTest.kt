package com.brunooliveira.meuorcamentomensal.domain.usecase

import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus
import com.brunooliveira.meuorcamentomensal.domain.repository.ExpenseRepository
import com.google.common.base.Verify.verify
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetAllExpensesUseCaseTest {

    private lateinit var repository: ExpenseRepository
    private lateinit var useCase: GetAllExpensesUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = GetAllExpensesUseCase(repository)
    }

    @Test
    fun `should return expenses flow from repository`() = runTest {
        val expenses = listOf(
            Expense(1, "Internet", 150.0, LocalDate.now(), PaymentStatus.PENDING)
        )

        val flow = flowOf(expenses)

        coEvery { repository.getAll() } returns flow

        val result = useCase().first()

        assertThat(result).isEqualTo(expenses)
        coVerify(exactly = 1) { repository.getAll() }
    }

}