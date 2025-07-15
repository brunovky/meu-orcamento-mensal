package com.brunooliveira.meuorcamentomensal.domain.usecase

import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus
import com.brunooliveira.meuorcamentomensal.domain.repository.ExpenseRepository

class CopyExpensesUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(
        expensesToCopy: List<Expense>,
        targetYear: Int,
        targetMonth: Int
    ): List<Long> {
        if (expensesToCopy.isEmpty()) return emptyList()

        val copiedExpenses = expensesToCopy.map { expense ->
            val newDueDate = expense.dueDate.withYear(targetYear).withMonth(targetMonth)

            expense.copy(
                id = 0, // Gera novo ID
                dueDate = newDueDate,
                status = PaymentStatus.PENDING
            )
        }

        return repository.insertAll(copiedExpenses)
    }
}