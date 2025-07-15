package com.brunooliveira.meuorcamentomensal.data.repository

import com.brunooliveira.meuorcamentomensal.data.local.ExpenseDao
import com.brunooliveira.meuorcamentomensal.data.mapper.toDomain
import com.brunooliveira.meuorcamentomensal.data.mapper.toEntity
import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(private val dao: ExpenseDao): ExpenseRepository {

    override fun getAll(): Flow<List<Expense>> {
        return dao.getAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insert(expense: Expense): Long {
        return dao.insert(expense.toEntity())
    }

    override suspend fun insertAll(expenses: List<Expense>): List<Long> {
        return dao.insertAll(expenses.map { it.toEntity() })
    }

    override suspend fun delete(expense: Expense) {
        dao.delete(expense.toEntity())
    }

}