package com.brunooliveira.meuorcamentomensal.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.brunooliveira.meuorcamentomensal.data.mapper.toEntity
import com.brunooliveira.meuorcamentomensal.domain.model.Expense
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class ExpenseDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ExpenseDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.expenseDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `should insert and get expenses successfully`() = runBlocking {
        val expense = Expense(
            id = 0,
            name = "Internet",
            amount = 150.0,
            dueDate = LocalDate.of(2025, 8, 10),
            status = PaymentStatus.PENDING
        )

        dao.insert(expense.toEntity())

        val result = dao.getAll().first()

        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("Internet")
    }

    @Test
    fun `should delete expense and remove from database`() = runBlocking {
        var expense = Expense(
            id = 0,
            name = "Aluguel",
            amount = 2000.0,
            dueDate = LocalDate.of(2025, 8, 5),
            status = PaymentStatus.PAID
        )

        val generatedId = dao.insert(expense.toEntity())
        expense = expense.copy(id = generatedId.toInt())
        dao.delete(expense.toEntity())

        val result = dao.getAll().first()

        assertThat(result).isEmpty()
    }
}