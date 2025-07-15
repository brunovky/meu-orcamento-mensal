package com.brunooliveira.meuorcamentomensal.domain.usecase

import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus
import com.brunooliveira.meuorcamentomensal.domain.repository.ExpenseRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DeleteExpenseUseCaseTest {

    private lateinit var repository: ExpenseRepository
    private lateinit var useCase: DeleteExpenseUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = DeleteExpenseUseCase(repository)
    }

    @Test
    fun `should delete expense`() = runTest {
        val expense = Expense(1, "Internet", 150.0, LocalDate.now(), PaymentStatus.PENDING)

        coEvery { repository.delete(expense) } just Runs

        useCase(expense)

        coVerify(exactly = 1) { repository.delete(expense) }
    }

}