package com.brunooliveira.meuorcamentomensal.notification

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.brunooliveira.meuorcamentomensal.notification.NotificationPreferencesKeys.DEFAULT_HOUR
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PREFS_NAME = "notification_prefs"
val Context.notificationDataStore by preferencesDataStore(name = PREFS_NAME)

object NotificationPreferencesKeys {
    val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
    const val DEFAULT_HOUR = 9
}

class NotificationPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val notificationHourFlow: Flow<Int> = context.notificationDataStore.data
        .map { prefs -> prefs[NotificationPreferencesKeys.NOTIFICATION_HOUR] ?: DEFAULT_HOUR }

    suspend fun setNotificationHour(hour: Int) {
        context.notificationDataStore.edit { prefs ->
            prefs[NotificationPreferencesKeys.NOTIFICATION_HOUR] = hour
        }
    }
}
