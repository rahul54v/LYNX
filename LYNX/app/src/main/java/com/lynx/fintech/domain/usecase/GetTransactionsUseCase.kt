package com.lynx.fintech.domain.usecase

import com.lynx.fintech.data.repository.TransactionRepository
import com.lynx.fintech.domain.model.Transaction
import com.lynx.fintech.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase(private val repository: TransactionRepository) {
    operator fun invoke(): Flow<List<Transaction>> {
        return repository.getAllTransactions()
    }

    fun byType(type: TransactionType): Flow<List<Transaction>> {
        return repository.getTransactionsByType(type)
    }
}
