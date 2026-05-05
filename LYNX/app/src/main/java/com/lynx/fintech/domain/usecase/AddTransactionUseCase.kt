package com.lynx.fintech.domain.usecase

import com.lynx.fintech.data.repository.TransactionRepository
import com.lynx.fintech.domain.model.Transaction

class AddTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction): Long {
        return repository.addTransaction(transaction)
    }
}
