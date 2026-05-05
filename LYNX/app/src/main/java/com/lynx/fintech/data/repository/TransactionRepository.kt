package com.lynx.fintech.data.repository

import com.lynx.fintech.data.local.LynxDatabase
import com.lynx.fintech.data.local.entity.TransactionEntity
import com.lynx.fintech.domain.model.CategoryBreakdown
import com.lynx.fintech.domain.model.DashboardData
import com.lynx.fintech.domain.model.MonthlyDataPoint
import com.lynx.fintech.domain.model.Transaction
import com.lynx.fintech.domain.model.TransactionCategory
import com.lynx.fintech.domain.model.TransactionType
import com.lynx.fintech.domain.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionRepository(private val database: LynxDatabase) {

    private val transactionDao = database.transactionDao()

    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getRecentTransactions(limit: Int = 5): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactions(limit).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getDashboardData(): Flow<DashboardData> {
        val incomeFlow = transactionDao.getTotalByType(TransactionType.INCOME.name)
            .map { it ?: 0.0 }
        val expenseFlow = transactionDao.getTotalByType(TransactionType.EXPENSE.name)
            .map { it ?: 0.0 }
        val recentFlow = transactionDao.getRecentTransactions(5)
            .map { it.map { entity -> entity.toDomainModel() } }
        val categoryFlow = transactionDao.getCategoryTotals()

        return combine(incomeFlow, expenseFlow, recentFlow, categoryFlow) { income, expense, recent, categoryTotals ->
            val totalBalance = income - expense
            val totalExpense = expense

            val categoryBreakdown = if (totalExpense > 0) {
                categoryTotals.map { categoryTotal ->
                    val category = TransactionCategory.entries.find { it.name == categoryTotal.category }
                        ?: TransactionCategory.OTHER
                    CategoryBreakdown(
                        category = category,
                        amount = categoryTotal.total,
                        percentage = (categoryTotal.total / totalExpense).toFloat()
                    )
                }
            } else {
                emptyList()
            }

            val monthlyData = generateMonthlyData()

            DashboardData(
                totalBalance = totalBalance,
                totalIncome = income,
                totalExpenses = expense,
                recentTransactions = recent,
                categoryBreakdown = categoryBreakdown,
                monthlyData = monthlyData
            )
        }
    }

    private fun generateMonthlyData(): List<MonthlyDataPoint> {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("MMM", Locale.getDefault())
        val data = mutableListOf<MonthlyDataPoint>()

        for (i in 5 downTo 0) {
            val tempCal = calendar.clone() as Calendar
            tempCal.add(Calendar.MONTH, -i)
            val monthName = format.format(tempCal.time)
            data.add(
                MonthlyDataPoint(
                    month = monthName,
                    income = 0.0,
                    expense = 0.0
                )
            )
        }
        return data
    }

    suspend fun addTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction.toEntity())
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    private fun TransactionEntity.toDomainModel(): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            description = description,
            category = TransactionCategory.entries.find { it.name == category } ?: TransactionCategory.OTHER,
            type = TransactionType.entries.find { it.name == type } ?: TransactionType.EXPENSE,
            date = date,
            currency = Currency.entries.find { it.code == currency } ?: Currency.USD
        )
    }

    private fun Transaction.toEntity(): TransactionEntity {
        return TransactionEntity(
            id = id,
            amount = amount,
            description = description,
            category = category.name,
            type = type.name,
            date = date,
            currency = currency.code
        )
    }
}
