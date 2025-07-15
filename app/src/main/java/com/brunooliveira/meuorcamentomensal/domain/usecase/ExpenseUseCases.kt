package com.brunooliveira.meuorcamentomensal.domain.usecase

import com.brunooliveira.meuorcamentomensal.domain.repository.ExpenseRepository

data class ExpenseUseCases(
    val getAllExpenses: GetAllExpensesUseCase,
    val insertExpense: InsertExpenseUseCase,
    val deleteExpense: DeleteExpenseUseCase,
    val copyExpenses: CopyExpensesUseCase
) {
    companion object {
        fun create(repository: ExpenseRepository): ExpenseUseCases {
            return ExpenseUseCases(
                getAllExpenses = GetAllExpensesUseCase(repository),
                insertExpense = InsertExpenseUseCase(repository),
                deleteExpense = DeleteExpenseUseCase(repository),
                copyExpenses = CopyExpensesUseCase(repository)
            )
        }
    }
}

