package com.brunooliveira.meuorcamentomensal.presentation.settings

import com.brunooliveira.meuorcamentomensal.domain.usecase.ExpenseUseCases
import com.brunooliveira.meuorcamentomensal.notification.ExpenseNotifier
import com.brunooliveira.meuorcamentomensal.notification.NotificationPreferences
import com.brunooliveira.meuorcamentomensal.presentation.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private lateinit var prefs: NotificationPreferences
    private lateinit var useCases: ExpenseUseCases
    private lateinit var notifier: ExpenseNotifier
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        useCases = mockk(relaxed = true)
        notifier = mockk(relaxed = true)
        prefs = mockk()

        coEvery { prefs.notificationHourFlow } returns flowOf(9)

        viewModel = SettingsViewModel(prefs, useCases, notifier)
    }

    @Test
    fun `should save hour and reschedule notifications`() = runTest {
        // Arrange
        val hour = 10

        coEvery { prefs.setNotificationHour(hour) } just Runs
        coEvery { notifier.rescheduleAllNotifications(useCases, hour) } just Runs

        // Act
        viewModel.saveHour(hour)

        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { prefs.setNotificationHour(hour) }
        coVerify(exactly = 1) { notifier.rescheduleAllNotifications(useCases, hour) }
    }
}