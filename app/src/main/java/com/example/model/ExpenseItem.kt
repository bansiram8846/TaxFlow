package com.example.model

enum class ExpenseIconType {
    DESIGN,
    CAFE,
    CAR,
    CHAIR,
    SERVER,
    EQUIPMENT,
    FUEL,
    RECEIPT_GENERIC
}

data class ExpenseItem(
    val id: String,
    val vendor: String,
    val subtitle: String,
    val category: String,
    val dateText: String,
    val amountInBaseInr: Double,
    val taxSavedInBaseInr: Double,
    val deductiblePercentage: Int, // e.g. 100, 50
    val deductibleBadgeText: String, // e.g. "100% Deductible", "50% Deductible (Meals)", "Depreciation / 100%"
    val statusTagText: String, // e.g. "AI Verified", "AI Categorized", "Receipt Attached", "Verified", "Auto-synced via HDFC Bank", "Needs Review"
    val iconType: ExpenseIconType = ExpenseIconType.RECEIPT_GENERIC,
    val isNeedsReview: Boolean = false,
    val receiptNote: String? = null
)
