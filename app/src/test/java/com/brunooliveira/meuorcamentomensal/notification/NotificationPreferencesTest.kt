package com.brunooliveira.meuorcamentomensal.notification

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotificationPreferencesTest {

    private lateinit var context: Context
    private lateinit var notificationPreferences: NotificationPreferences

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        notificationPreferences = NotificationPreferences(context)
    }

    @Test
    fun `should return default hour when no value is set`() = runTest {
        val hour = notificationPreferences.notificationHourFlow.first()
        assertThat(hour).isEqualTo(NotificationPreferencesKeys.DEFAULT_HOUR)
    }

    @Test
    fun `should save and retrieve custom notification hour`() = runTest {
        val customHour = 15

        notificationPreferences.setNotificationHour(customHour)
        val savedHour = notificationPreferences.notificationHourFlow.first()

        assertThat(savedHour).isEqualTo(customHour)
    }

}