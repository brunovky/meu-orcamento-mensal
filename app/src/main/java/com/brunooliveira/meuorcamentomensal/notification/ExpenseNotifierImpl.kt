package com.brunooliveira.meuorcamentomensal.notification

import android.content.Context
import com.brunooliveira.meuorcamentomensal.domain.usecase.ExpenseUseCases
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject

class ExpenseNotifierImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ExpenseNotifier {

    override suspend fun scheduleNotification(expenseId: Int, expenseName: String, dueDate: LocalDate, notificationHour: Int) {
        scheduleExpenseNotification(
            context = context,
            expenseId = expenseId,
            expenseName = expenseName,
            dueDate = dueDate,
            notificationHour = notificationHour
        )
    }

    override suspend fun cancelNotification(expenseId: Int) {
        cancelExpenseNotification(context, expenseId)
    }

    override suspend fun rescheduleAllNotifications(
        useCases: ExpenseUseCases,
        notificationHour: Int
    ) {
        rescheduleAllExpenseNotifications(context, useCases, notificationHour)
    }
}