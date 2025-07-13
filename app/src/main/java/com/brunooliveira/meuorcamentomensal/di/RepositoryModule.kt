package com.brunooliveira.meuorcamentomensal.di

import com.brunooliveira.meuorcamentomensal.data.local.ExpenseDao
import com.brunooliveira.meuorcamentomensal.data.repository.ExpenseRepositoryImpl
import com.brunooliveira.meuorcamentomensal.domain.repository.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideExpenseRepository(
        dao: ExpenseDao
    ): ExpenseRepository = ExpenseRepositoryImpl(dao)
}