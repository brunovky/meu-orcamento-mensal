package com.brunooliveira.meuorcamentomensal.di

import com.brunooliveira.meuorcamentomensal.domain.repository.ExpenseRepository
import com.brunooliveira.meuorcamentomensal.domain.usecase.CopyExpensesUseCase
import com.brunooliveira.meuorcamentomensal.domain.usecase.DeleteExpenseUseCase
import com.brunooliveira.meuorcamentomensal.domain.usecase.ExpenseUseCases
import com.brunooliveira.meuorcamentomensal.domain.usecase.GetAllExpensesUseCase
import com.brunooliveira.meuorcamentomensal.domain.usecase.InsertExpenseUseCase
import com.brunooliveira.meuorcamentomensal.domain.usecase.UpdateExpenseStatusUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideExpenseUseCases(
        repository: ExpenseRepository
    ): ExpenseUseCases {
        return ExpenseUseCases(
            getAllExpenses = GetAllExpensesUseCase(repository),
            insertExpense = InsertExpenseUseCase(repository),
            deleteExpense = DeleteExpenseUseCase(repository),
            updateExpenseStatus = UpdateExpenseStatusUseCase(repository),
            copyExpenses = CopyExpensesUseCase(repository)
        )
    }
}