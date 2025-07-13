package com.brunooliveira.meuorcamentomensal

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.brunooliveira.meuorcamentomensal.presentation.add.AddExpenseScreen
import com.brunooliveira.meuorcamentomensal.presentation.edit.EditExpenseScreen
import com.brunooliveira.meuorcamentomensal.presentation.home.HomeScreen
import com.brunooliveira.meuorcamentomensal.presentation.settings.SettingsScreen
import com.brunooliveira.meuorcamentomensal.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class Screen {
    object Home : Screen()
    object AddExpense : Screen()
    data class EditExpense(val expenseId: Int) : Screen()
    object Settings : Screen()
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Aqui pode tratar se o usuário negou a permissão, se quiser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicita permissão para notificações no Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            AppTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        val direction = if (
                            (initialState is Screen.Home && targetState is Screen.AddExpense) ||
                            (initialState is Screen.Home && targetState is Screen.EditExpense) ||
                            (initialState is Screen.Home && targetState is Screen.Settings)
                        ) 1 else -1

                        slideInHorizontally { direction * it } + fadeIn() togetherWith
                                slideOutHorizontally { -direction * it } + fadeOut()
                    },
                    label = "Screen Transition"
                ) { screen ->
                    when (screen) {
                        is Screen.Home -> HomeScreen(
                            onAddExpenseClick = { currentScreen = Screen.AddExpense },
                            onEditExpenseClick = { expenseId ->
                                currentScreen = Screen.EditExpense(expenseId)
                            },
                            onSettingsClick = { currentScreen = Screen.Settings }
                        )

                        is Screen.AddExpense -> AddExpenseScreen(
                            onBack = { currentScreen = Screen.Home }
                        )

                        is Screen.EditExpense -> EditExpenseScreen(
                            expenseId = screen.expenseId,
                            onBack = { currentScreen = Screen.Home }
                        )

                        is Screen.Settings -> SettingsScreen(
                            onBack = { currentScreen = Screen.Home }
                        )
                    }
                }
            }
        }
    }
}
