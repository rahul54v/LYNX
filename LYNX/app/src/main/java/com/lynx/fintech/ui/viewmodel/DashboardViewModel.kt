package com.lynx.fintech.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lynx.fintech.LynxApplication
import com.lynx.fintech.domain.model.DashboardData
import com.lynx.fintech.domain.model.LynxUiState
import com.lynx.fintech.domain.model.Transaction
import com.lynx.fintech.domain.usecase.DeleteTransactionUseCase
import com.lynx.fintech.domain.usecase.GetDashboardDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LynxUiState<DashboardData>>(LynxUiState.Loading)
    val uiState: StateFlow<LynxUiState<DashboardData>> = _uiState

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            getDashboardDataUseCase()
                .onStart {
                    _uiState.value = LynxUiState.Loading
                }
                .catch { exception ->
                    _uiState.value = LynxUiState.Error(
                        message = exception.message ?: "Failed to load dashboard data"
                    )
                }
                .collect { dashboardData ->
                    if (dashboardData.totalIncome == 0.0 && dashboardData.totalExpenses == 0.0) {
                        _uiState.value = LynxUiState.Empty(
                            message = "No transactions found. Start tracking your finances."
                        )
                    } else {
                        _uiState.value = LynxUiState.Success(data = dashboardData)
                    }
                }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                deleteTransactionUseCase(transaction)
            } catch (e: Exception) {
                _uiState.value = LynxUiState.Error(
                    message = e.message ?: "Failed to delete transaction"
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LynxApplication)
                DashboardViewModel(
                    getDashboardDataUseCase = application.appContainer.getDashboardDataUseCase,
                    deleteTransactionUseCase = application.appContainer.deleteTransactionUseCase
                )
            }
        }
    }
}
