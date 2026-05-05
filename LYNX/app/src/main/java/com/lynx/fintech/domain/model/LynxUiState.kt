package com.lynx.fintech.domain.model

sealed class LynxUiState<out T> {
    data object Loading : LynxUiState<Nothing>()
    data class Success<T>(val data: T) : LynxUiState<T>()
    data class Empty(val message: String = "No data available") : LynxUiState<Nothing>()
    data class Error(val message: String) : LynxUiState<Nothing>()
}
