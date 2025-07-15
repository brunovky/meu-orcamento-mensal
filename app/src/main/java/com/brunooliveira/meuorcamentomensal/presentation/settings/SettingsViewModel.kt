package com.brunooliveira.meuorcamentomensal.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunooliveira.meuorcamentomensal.domain.usecase.ExpenseUseCases
import com.brunooliveira.meuorcamentomensal.notification.ExpenseNotifier
import com.brunooliveira.meuorcamentomensal.notification.NotificationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val notificationPreferences: NotificationPreferences,
    private val useCases: ExpenseUseCases,
    private val notifier: ExpenseNotifier
) : ViewModel() {

    val notificationHourFlow = notificationPreferences.notificationHourFlow

    fun saveHour(hour: Int) {
        viewModelScope.launch {
            notificationPreferences.setNotificationHour(hour)
            notifier.rescheduleAllNotifications(
                useCases = useCases,
                notificationHour = hour
            )
        }
    }
}