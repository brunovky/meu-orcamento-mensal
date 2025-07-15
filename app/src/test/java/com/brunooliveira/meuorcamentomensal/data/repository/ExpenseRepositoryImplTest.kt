package com.brunooliveira.meuorcamentomensal.data.repository

import app.cash.turbine.test
import com.brunooliveira.meuorcamentomensal.data.local.ExpenseDao
import com.brunooliveira.meuorcamentomensal.data.local.ExpenseEntity
import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseRepositoryImplTest {

    private lateinit var dao: ExpenseDao
    private lateinit var repository: ExpenseRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = ExpenseRepositoryImpl(dao)
    }

    @Test
    fun `getAll should return mapped domain expenses`() = runTest {
        val entityList = listOf(
            ExpenseEntity(1, "Internet", 150.0, LocalDate.of(2025, 7, 10), PaymentStatus.PENDING.name, null),
            ExpenseEntity(2, "Aluguel", 2000.0, LocalDate.of(2025, 7, 5), PaymentStatus.PAID.name, null)
        )

        coEvery { dao.getAll() } returns flowOf(entityList)

        repository.getAll().test {
            val result = awaitItem()
            assertThat(result).hasSize(2)
            assertThat(result[0].name).isEqualTo("Internet")
            assertThat(result[1].name).isEqualTo("Aluguel")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insert should call dao and return generated id`() = runTest {
        val expense = Expense(0, "Netflix", 39.90, LocalDate.of(2025, 7, 15), PaymentStatus.PENDING)

        coEvery { dao.insert(any()) } returns 10L

        val result = repository.insert(expense)

        assertThat(result).isEqualTo(10L)
        coVerify { dao.insert(match { it.name == "Netflix" && it.amount == 39.90 }) }
    }

    @Test
    fun `insertAll should call dao and return generated ids`() = runTest {
        val expenses = listOf(
            Expense(0, "Spotify", 19.90, LocalDate.of(2025, 7, 12), PaymentStatus.PENDING),
            Expense(0, "Gym", 100.0, LocalDate.of(2025, 7, 20), PaymentStatus.PENDING)
        )

        coEvery { dao.insertAll(any()) } returns listOf(1L, 2L)

        val result = repository.insertAll(expenses)

        assertThat(result).isEqualTo(listOf(1L, 2L))
        coVerify {
            dao.insertAll(match { it.size == 2 && it[0].name == "Spotify" && it[1].name == "Gym" })
        }
    }

    @Test
    fun `delete should call dao with correct entity`() = runTest {
        val expense = Expense(5, "Internet", 150.0, LocalDate.of(2025, 7, 10), PaymentStatus.PAID)

        repository.delete(expense)

        coVerify {
            dao.delete(match { it.id == 5 && it.name == "Internet" })
        }
    }
}
