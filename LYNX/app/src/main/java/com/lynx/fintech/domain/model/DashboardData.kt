package com.lynx.fintech.domain.model

data class DashboardData(
    val totalBalance: Double,
    val totalIncome: Double,
    val totalExpenses: Double,
    val recentTransactions: List<Transaction>,
    val categoryBreakdown: List<CategoryBreakdown>,
    val monthlyData: List<MonthlyDataPoint>
)

data class CategoryBreakdown(
    val category: TransactionCategory,
    val amount: Double,
    val percentage: Float
)

data class MonthlyDataPoint(
    val month: String,
    val income: Double,
    val expense: Double
)
