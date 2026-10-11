package com.example.model

import java.text.NumberFormat
import java.util.Locale

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val pillLabel: String,
    val flag: String,
    // Base is INR (1.0). Exchange rates relative to INR:
    val rateFromInr: Double,
    val countryComplianceNote: String
) {
    USD("USD", "$", "USD ($)", "🇺🇸", 0.01205, "US IRS Tax Year 2025 • Schedule C Form 1040"),
    EUR("EUR", "€", "EUR (€)", "🇪🇺", 0.01108, "EU Standard Accounting • VAT Compliance"),
    INR("INR", "₹", "INR (₹)", "🇮🇳", 1.0, "India FY 2024-25 • Sec 37(1) Deductions"),
    GBP("GBP", "£", "GBP (£)", "🇬🇧", 0.00945, "UK HMRC Compliance • Self Assessment"),
    JPY("JPY", "¥", "JPY (¥)", "🇯🇵", 1.8150, "NTA Japan Blue Return (Ao-iro Shinkoku)");

    fun format(amountInBaseInr: Double): String {
        val converted = amountInBaseInr * rateFromInr
        return when (this) {
            INR -> {
                // Indian numbering format (e.g. ₹1,42,850)
                val rounded = Math.round(converted).toLong()
                val isNegative = rounded < 0
                val absVal = Math.abs(rounded)
                val s = absVal.toString()
                val formatted = if (s.length <= 3) {
                    s
                } else {
                    val lastThree = s.substring(s.length - 3)
                    val rest = s.substring(0, s.length - 3)
                    val parts = mutableListOf<String>()
                    var idx = rest.length
                    while (idx > 0) {
                        val start = maxOf(0, idx - 2)
                        parts.add(0, rest.substring(start, idx))
                        idx -= 2
                    }
                    parts.joinToString(",") + "," + lastThree
                }
                if (isNegative) "-₹$formatted" else "₹$formatted"
            }
            JPY -> {
                val rounded = Math.round(converted).toLong()
                "¥" + NumberFormat.getIntegerInstance(Locale.JAPAN).format(rounded)
            }
            USD -> {
                val rounded = Math.round(converted).toLong()
                "$" + NumberFormat.getIntegerInstance(Locale.US).format(rounded)
            }
            EUR -> {
                val rounded = Math.round(converted).toLong()
                "€" + NumberFormat.getIntegerInstance(Locale.GERMANY).format(rounded)
            }
            GBP -> {
                val rounded = Math.round(converted).toLong()
                "£" + NumberFormat.getIntegerInstance(Locale.UK).format(rounded)
            }
        }
    }

    fun formatCompact(amountInBaseInr: Double): String {
        return format(amountInBaseInr)
    }
}
