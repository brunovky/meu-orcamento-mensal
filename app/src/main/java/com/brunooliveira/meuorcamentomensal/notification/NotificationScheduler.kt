package com.brunooliveira.meuorcamentomensal.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.brunooliveira.meuorcamentomensal.domain.usecase.ExpenseUseCases
import com.brunooliveira.meuorcamentomensal.data.worker.ExpenseReminderWorker
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

fun scheduleExpenseNotification(context: Context, expenseId: Int, expenseName: String, dueDate: LocalDate, notificationHour: Int) {
    val now = LocalDateTime.now()
    val notificationDateTime = dueDate.atTime(notificationHour, 0) // 9h da manhã

    val delay = if (notificationDateTime.isAfter(now)) {
        Duration.between(now, notificationDateTime).toMillis()
    } else {
        0L
    }

    val data = Data.Builder()
        .putInt("expenseId", expenseId)
        .putString("expenseName", expenseName)
        .build()

    val workName = "expense_notification_${expenseId}"

    val workRequest = OneTimeWorkRequestBuilder<ExpenseReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .addTag(workName)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
}

fun cancelExpenseNotification(context: Context, expenseId: Int) {
    val workName = "expense_notification_$expenseId"
    WorkManager.getInstance(context).cancelUniqueWork(workName)
}

suspend fun rescheduleAllExpenseNotifications(
    context: Context,
    useCases: ExpenseUseCases,
    notificationHour: Int
) {
    val expenses = useCases.getAllExpenses().first()

    expenses.forEach { expense ->
        cancelExpenseNotification(context, expense.id) // cancela atual
        if (expense.status.name != "PAID") {
            scheduleExpenseNotification(
                context = context,
                expenseId = expense.id,
                expenseName = expense.name,
                dueDate = expense.dueDate,
                notificationHour = notificationHour
            )
        }
    }
}
