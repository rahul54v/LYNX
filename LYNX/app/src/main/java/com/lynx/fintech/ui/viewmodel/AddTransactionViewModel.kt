package com.lynx.fintech.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lynx.fintech.LynxApplication
import com.lynx.fintech.domain.model.Currency
import com.lynx.fintech.domain.model.Transaction
import com.lynx.fintech.domain.model.TransactionCategory
import com.lynx.fintech.domain.model.TransactionType
import com.lynx.fintech.domain.model.ValidationResult
import com.lynx.fintech.domain.usecase.AddTransactionUseCase
import com.lynx.fintech.domain.usecase.ValidateTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTransactionViewModel(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val validateTransactionUseCase: ValidateTransactionUseCase
) : ViewModel() {

    sealed class AddTransactionUiState {
        data object Idle : AddTransactionUiState()
        data object Loading : AddTransactionUiState()
        data object Success : AddTransactionUiState()
        data class Error(val message: String) : AddTransactionUiState()
    }

    private val _uiState = MutableStateFlow<AddTransactionUiState>(AddTransactionUiState.Idle)
    val uiState: StateFlow<AddTransactionUiState> = _uiState

    var amount by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var transactionType by mutableStateOf(TransactionType.EXPENSE)
        private set
    var category by mutableStateOf(TransactionCategory.OTHER)
        private set
    var date by mutableStateOf(System.currentTimeMillis())
        private set
    var currency by mutableStateOf(Currency.USD)
        private set
    var amountError by mutableStateOf<String?>(null)
        private set
    var descriptionError by mutableStateOf<String?>(null)
        private set

    fun setAmount(value: String) {
        amount = value
        amountError = null
    }

    fun setDescription(value: String) {
        description = value
        descriptionError = null
    }

    fun setTransactionType(type: TransactionType) {
        transactionType = type
    }

    fun setCategory(selectedCategory: TransactionCategory) {
        category = selectedCategory
    }

    fun setDate(selectedDate: Long) {
        date = selectedDate
    }

    fun setCurrency(selectedCurrency: Currency) {
        currency = selectedCurrency
    }

    fun getFormattedDate(): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return formatter.format(Date(date))
    }

    fun saveTransaction() {
        val validation = validateTransactionUseCase(amount, description)

        when (validation) {
            is ValidationResult.Error -> {
                if (validation.message.contains("amount", ignoreCase = true) ||
                    validation.message.contains("valid", ignoreCase = true)) {
                    amountError = validation.message
                } else {
                    descriptionError = validation.message
                }
                return
            }
            is ValidationResult.Success -> {
                amountError = null
                descriptionError = null
            }
        }

        val parsedAmount = amount.toDoubleOrNull()
        if (parsedAmount == null || parsedAmount <= 0) {
            amountError = "Please enter a valid amount"
            return
        }

        val transaction = Transaction(
            amount = parsedAmount,
            description = description,
            category = category,
            type = transactionType,
            date = date,
            currency = currency
        )

        viewModelScope.launch {
            _uiState.value = AddTransactionUiState.Loading
            try {
                addTransactionUseCase(transaction)
                _uiState.value = AddTransactionUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddTransactionUiState.Error(
                    message = e.message ?: "Failed to save transaction"
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LynxApplication)
                AddTransactionViewModel(
                    addTransactionUseCase = application.appContainer.addTransactionUseCase,
                    validateTransactionUseCase = application.appContainer.validateTransactionUseCase
                )
            }
        }
    }
}
