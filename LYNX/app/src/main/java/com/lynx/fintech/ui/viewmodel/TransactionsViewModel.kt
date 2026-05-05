package com.lynx.fintech.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lynx.fintech.LynxApplication
import com.lynx.fintech.domain.model.LynxUiState
import com.lynx.fintech.domain.model.Transaction
import com.lynx.fintech.domain.usecase.DeleteTransactionUseCase
import com.lynx.fintech.domain.usecase.GetTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LynxUiState<List<Transaction>>>(LynxUiState.Loading)
    val uiState: StateFlow<LynxUiState<List<Transaction>>> = _uiState

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            getTransactionsUseCase()
                .onStart {
                    _uiState.value = LynxUiState.Loading
                }
                .catch { exception ->
                    _uiState.value = LynxUiState.Error(
                        message = exception.message ?: "Failed to load transactions"
                    )
                }
                .collect { transactions ->
                    if (transactions.isEmpty()) {
                        _uiState.value = LynxUiState.Empty(
                            message = "No transactions found"
                        )
                    } else {
                        _uiState.value = LynxUiState.Success(data = transactions)
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
                TransactionsViewModel(
                    getTransactionsUseCase = application.appContainer.getTransactionsUseCase,
                    deleteTransactionUseCase = application.appContainer.deleteTransactionUseCase
                )
            }
        }
    }
}
