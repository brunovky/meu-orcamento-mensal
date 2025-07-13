package com.brunooliveira.meuorcamentomensal.data.mapper

import com.brunooliveira.meuorcamentomensal.data.local.ExpenseEntity
import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus

fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        name = name,
        amount = amount,
        dueDate = dueDate,
        status = PaymentStatus.valueOf(status),
        category = category
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        name = name,
        amount = amount,
        dueDate = dueDate,
        status = status.name,
        category = category
    )
}