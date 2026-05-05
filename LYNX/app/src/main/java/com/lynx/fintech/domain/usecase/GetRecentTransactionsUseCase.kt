package com.lynx.fintech.domain.usecase

import com.lynx.fintech.data.repository.TransactionRepository
import com.lynx.fintech.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

class GetRecentTransactionsUseCase(private val repository: TransactionRepository) {
    operator fun invoke(limit: Int = 5): Flow<List<Transaction>> {
        return repository.getRecentTransactions(limit)
    }
}
