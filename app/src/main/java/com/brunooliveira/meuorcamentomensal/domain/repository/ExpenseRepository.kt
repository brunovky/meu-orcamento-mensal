package com.brunooliveira.meuorcamentomensal.domain.repository

import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    fun getAll(): Flow<List<Expense>>

    suspend fun insert(expense: Expense): Long

    suspend fun insertAll(expenses: List<Expense>): List<Long>

    suspend fun delete(expense: Expense)

}