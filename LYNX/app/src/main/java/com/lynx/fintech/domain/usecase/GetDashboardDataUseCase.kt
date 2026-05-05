package com.lynx.fintech.domain.usecase

import com.lynx.fintech.data.repository.TransactionRepository
import com.lynx.fintech.domain.model.DashboardData
import kotlinx.coroutines.flow.Flow

class GetDashboardDataUseCase(private val repository: TransactionRepository) {
    operator fun invoke(): Flow<DashboardData> {
        return repository.getDashboardData()
    }
}
