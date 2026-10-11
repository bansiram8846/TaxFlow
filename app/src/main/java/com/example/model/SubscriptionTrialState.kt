package com.example.model

import java.util.concurrent.TimeUnit

data class SubscriptionTrialState(
    val installDateMillis: Long,
    val trialDurationDays: Int = 30,
    val isProSubscriber: Boolean = false,
    val isSimulatedExpired: Boolean = false
) {
    val daysElapsed: Int
        get() {
            val diffMillis = System.currentTimeMillis() - installDateMillis
            val days = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
            return maxOf(0, days)
        }

    val daysRemaining: Int
        get() {
            if (isSimulatedExpired) return 0
            if (isProSubscriber) return 365
            val remaining = trialDurationDays - daysElapsed
            return maxOf(0, remaining)
        }

    val isTrialActive: Boolean
        get() {
            if (isProSubscriber) return true
            if (isSimulatedExpired) return false
            return daysRemaining > 0
        }

    val canUseAiScanning: Boolean
        get() = isTrialActive

    val canLogUnlimitedMileage: Boolean
        get() = isTrialActive

    val statusBadgeText: String
        get() = when {
            isProSubscriber -> "Pro Plan Active"
            isSimulatedExpired -> "Trial Expired (0 Days)"
            daysRemaining > 0 -> "Free Access: $daysRemaining Days Left"
            else -> "Trial Expired"
        }
}
