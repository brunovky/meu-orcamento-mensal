package com.brunooliveira.meuorcamentomensal.domain.usecase

import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus
import com.brunooliveira.meuorcamentomensal.domain.repository.ExpenseRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class InsertExpenseUseCaseTest {

    private lateinit var repository: ExpenseRepository
    private lateinit var useCase: InsertExpenseUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = InsertExpenseUseCase(repository)
    }

    @Test
    fun `should insert expense and return generated id`() = runTest {
        val expense = Expense(0, "Luz", 100.0, LocalDate.now(), PaymentStatus.PENDING)

        coEvery { repository.insert(expense) } returns 42L

        val result = useCase(expense)

        assertThat(result).isEqualTo(42L)
        coVerify(exactly = 1) { repository.insert(expense) }
    }

}