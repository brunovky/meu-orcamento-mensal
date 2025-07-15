package com.brunooliveira.meuorcamentomensal.di

import android.content.Context
import com.brunooliveira.meuorcamentomensal.notification.ExpenseNotifier
import com.brunooliveira.meuorcamentomensal.notification.ExpenseNotifierImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotifierModule {

    @Provides
    @Singleton
    fun provideExpenseNotifier(
        @ApplicationContext context: Context
    ): ExpenseNotifier {
        return ExpenseNotifierImpl(context)
    }

}