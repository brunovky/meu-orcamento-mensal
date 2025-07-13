package com.brunooliveira.meuorcamentomensal.domain.model

import java.time.LocalDate

enum class PaymentStatus {
    PAID, PENDING
}

data class Expense(
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val dueDate: LocalDate,
    val status: PaymentStatus,
    val category: String? = null
)

fun PaymentStatus.toDisplayString(): String = when(this) {
    PaymentStatus.PAID -> "PAGO"
    PaymentStatus.PENDING -> "EM ANDAMENTO"
}