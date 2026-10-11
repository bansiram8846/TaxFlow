package com.example.data

data class ReceiptScanResult(
    val vendor: String,
    val totalAmount: Double,
    val currencyCode: String,
    val dateText: String,
    val category: String,
    val deductiblePercentage: Int,
    val taxSavingsEst: Double,
    val confidenceScore: Float,
    val rawExtractedLines: List<String>,
    val aiNote: String
)
