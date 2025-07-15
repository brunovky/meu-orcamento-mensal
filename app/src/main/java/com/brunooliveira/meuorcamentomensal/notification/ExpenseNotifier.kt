package com.brunooliveira.meuorcamentomensal.notification

import com.brunooliveira.meuorcamentomensal.domain.usecase.ExpenseUseCases
import java.time.LocalDate

interface ExpenseNotifier {

    suspend fun scheduleNotification(expenseId: Int, expenseName: String, dueDate: LocalDate, notificationHour: Int)

    suspend fun cancelNotification(expenseId: Int)

    suspend fun rescheduleAllNotifications(useCases: ExpenseUseCases, notificationHour: Int)

}