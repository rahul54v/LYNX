package com.lynx.fintech.domain.usecase

import com.lynx.fintech.domain.model.ValidationResult

class ValidateTransactionUseCase {
    operator fun invoke(amount: String, description: String): ValidationResult {
        if (amount.isBlank()) {
            return ValidationResult.Error("Please enter an amount")
        }

        val parsedAmount = amount.toDoubleOrNull()
        if (parsedAmount == null || parsedAmount <= 0) {
            return ValidationResult.Error("Please enter a valid positive amount")
        }

        if (description.isBlank()) {
            return ValidationResult.Error("Description cannot be empty")
        }

        if (description.length < 2) {
            return ValidationResult.Error("Description must be at least 2 characters")
        }

        return ValidationResult.Success
    }
}
