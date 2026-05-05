package com.lynx.fintech.di

import android.content.Context
import com.lynx.fintech.data.local.LynxDatabase
import com.lynx.fintech.data.repository.TransactionRepository
import com.lynx.fintech.domain.usecase.AddTransactionUseCase
import com.lynx.fintech.domain.usecase.DeleteTransactionUseCase
import com.lynx.fintech.domain.usecase.GetDashboardDataUseCase
import com.lynx.fintech.domain.usecase.GetRecentTransactionsUseCase
import com.lynx.fintech.domain.usecase.GetTransactionsUseCase
import com.lynx.fintech.domain.usecase.ValidateTransactionUseCase

class AppContainer(private val context: Context) {

    private val database: LynxDatabase by lazy {
        LynxDatabase.getDatabase(context)
    }

    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(database)
    }

    val getDashboardDataUseCase: GetDashboardDataUseCase by lazy {
        GetDashboardDataUseCase(transactionRepository)
    }

    val getTransactionsUseCase: GetTransactionsUseCase by lazy {
        GetTransactionsUseCase(transactionRepository)
    }

    val getRecentTransactionsUseCase: GetRecentTransactionsUseCase by lazy {
        GetRecentTransactionsUseCase(transactionRepository)
    }

    val addTransactionUseCase: AddTransactionUseCase by lazy {
        AddTransactionUseCase(transactionRepository)
    }

    val deleteTransactionUseCase: DeleteTransactionUseCase by lazy {
        DeleteTransactionUseCase(transactionRepository)
    }

    val validateTransactionUseCase: ValidateTransactionUseCase by lazy {
        ValidateTransactionUseCase()
    }
}
