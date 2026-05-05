package com.lynx.fintech.domain.model

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val category: TransactionCategory,
    val type: TransactionType,
    val date: Long,
    val currency: Currency = Currency.USD
)

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class TransactionCategory(val displayName: String, val colorHex: String) {
    SALARY("Salary", "#10B981"),
    INVESTMENT("Investment", "#3B82F6"),
    FREELANCE("Freelance", "#8B5CF6"),
    FOOD("Food & Dining", "#EF4444"),
    TRANSPORT("Transport", "#F97316"),
    SHOPPING("Shopping", "#EC4899"),
    ENTERTAINMENT("Entertainment", "#EAB308"),
    UTILITIES("Utilities", "#06B6D4"),
    HEALTH("Health", "#14B8A6"),
    EDUCATION("Education", "#6366F1"),
    TRAVEL("Travel", "#F43F5E"),
    SUBSCRIPTIONS("Subscriptions", "#A855F7"),
    GIFTS("Gifts & Donations", "#FB923C"),
    OTHER("Other", "#71717A")
}

enum class Currency(val symbol: String, val code: String) {
    USD("$", "USD"),
    EUR("€", "EUR"),
    GBP("£", "GBP"),
    JPY("¥", "JPY"),
    INR("₹", "INR"),
    KRW("₩", "KRW"),
    CNY("¥", "CNY"),
    AUD("A$", "AUD"),
    CAD("C$", "CAD"),
    CHF("Fr", "CHF"),
    SGD("S$", "SGD"),
    HKD("HK$", "HKD"),
    BRL("R$", "BRL"),
    MXN("Mex$", "MXN"),
    ZAR("R", "ZAR"),
    TRY("₺", "TRY"),
    AED("Dh", "AED"),
    SAR("﷼", "SAR")
}
