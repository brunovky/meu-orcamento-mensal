package com.brunooliveira.meuorcamentomensal.notification

import android.content.Context
import com.brunooliveira.meuorcamentomensal.domain.usecase.ExpenseUseCases
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ExpenseNotifierImplTest {

    private lateinit var context: Context
    private lateinit var useCases: ExpenseUseCases
    private lateinit var notifier: ExpenseNotifierImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        context = mockk(relaxed = true)
        useCases = mockk()

        notifier = ExpenseNotifierImpl(context)

        mockkStatic(::scheduleExpenseNotification)
        mockkStatic(::cancelExpenseNotification)
        mockkStatic(::rescheduleAllExpenseNotifications)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should call scheduleExpenseNotification with correct parameters`() = runTest {
        // Arrange
        val expenseId = 1
        val expenseName = "Internet"
        val dueDate = LocalDate.of(2025, 8, 10)
        val hour = 10

        coEvery {
            scheduleExpenseNotification(context, expenseId, expenseName, dueDate, hour)
        } just Runs

        // Act
        notifier.scheduleNotification(expenseId, expenseName, dueDate, hour)

        // Assert
        coVerify(exactly = 1) {
            scheduleExpenseNotification(context, expenseId, expenseName, dueDate, hour)
        }
    }

    @Test
    fun `should call cancelExpenseNotification with correct expenseId`() = runTest {
        // Arrange
        val expenseId = 1

        coEvery { cancelExpenseNotification(context, expenseId) } just Runs

        // Act
        notifier.cancelNotification(expenseId)

        // Assert
        coVerify(exactly = 1) { cancelExpenseNotification(context, expenseId) }
    }

    @Test
    fun `should call rescheduleAllExpenseNotifications`() = runTest {
        // Arrange
        val notificationHour = 10

        coEvery { rescheduleAllExpenseNotifications(context, useCases, notificationHour) } just Runs

        // Act
        notifier.rescheduleAllNotifications(useCases, notificationHour)

        // Assert
        coVerify(exactly = 1) {
            rescheduleAllExpenseNotifications(context, useCases, notificationHour)
        }
    }
}