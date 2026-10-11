package com.example.model

enum class DriveClassification {
    UNCLASSIFIED,
    BUSINESS,
    PERSONAL
}

data class MileageDrive(
    val id: String,
    val driveNumber: Int,
    val dateText: String, // e.g. "Drive on Oct 25, 2024"
    val timeRangeText: String, // e.g. "10:15 AM – 11:02 AM • Auto-Tracked"
    val startLocation: String, // e.g. "Home Office (Indiranagar)"
    val destinationLocation: String, // e.g. "Client HQ (Tech Park, Krmg.)"
    val distanceKm: Double, // e.g. 24.6
    val durationMins: Int, // e.g. 47
    val taxWriteOffInBaseInr: Double, // e.g. 615.0
    val aiSuggestion: String, // e.g. "AI suggests Business: Matched with Google Calendar invite \"Q4 Tax Sprint Review\"."
    val classification: DriveClassification = DriveClassification.UNCLASSIFIED,
    val timestamp: Long = System.currentTimeMillis()
)
