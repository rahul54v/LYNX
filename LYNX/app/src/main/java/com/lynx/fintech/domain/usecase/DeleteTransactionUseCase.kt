package com.lynx.fintech.domain.usecase

import com.lynx.fintech.data.repository.TransactionRepository
import com.lynx.fintech.domain.model.Transaction

class DeleteTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.deleteTransaction(transaction)
    }

    suspend fun byId(id: Long) {
        repository.deleteTransactionById(id)
    }
}
