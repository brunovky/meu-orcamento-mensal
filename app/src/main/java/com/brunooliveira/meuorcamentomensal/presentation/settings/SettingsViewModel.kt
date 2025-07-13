package com.brunooliveira.meuorcamentomensal.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunooliveira.meuorcamentomensal.notification.NotificationPreferences
import com.brunooliveira.meuorcamentomensal.domain.usecase.ExpenseUseCases
import com.brunooliveira.meuorcamentomensal.notification.rescheduleAllExpenseNotifications
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val notificationPreferences: NotificationPreferences,
    private val useCases: ExpenseUseCases,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val notificationHourFlow = notificationPreferences.notificationHourFlow

    fun saveHour(hour: Int) {
        viewModelScope.launch {
            notificationPreferences.setNotificationHour(hour)
            rescheduleAllExpenseNotifications(
                context = context,
                useCases = useCases,
                notificationHour = hour
            )
        }
    }
}