package com.brunooliveira.meuorcamentomensal.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.brunooliveira.meuorcamentomensal.domain.usecase.ExpenseUseCases
import com.brunooliveira.meuorcamentomensal.notification.NotificationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@HiltWorker
class ExpenseReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val useCases: ExpenseUseCases
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val today = LocalDate.now()
            val expensesDueToday = useCases.getAllExpenses().first()
                .filter { it.dueDate == today && it.status.name != "PAID" }

            expensesDueToday.forEach { expense ->
                NotificationUtils.showNotification(
                    context = applicationContext,
                    title = "Gasto vencendo hoje",
                    message = "${expense.name} vence hoje"
                )
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
