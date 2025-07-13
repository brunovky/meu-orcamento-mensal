package com.brunooliveira.meuorcamentomensal.domain.usecase

import com.brunooliveira.meuorcamentomensal.domain.repository.ExpenseRepository

class UpdateExpenseStatusUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: Int, newStatus: String) {
        repository.updateStatus(id, newStatus)
    }
}