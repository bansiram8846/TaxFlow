package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppCurrency
import com.example.model.DriveClassification
import com.example.model.ExpenseIconType
import com.example.model.ExpenseItem
import com.example.model.MileageDrive
import com.example.model.SubscriptionTrialState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class TaxFlowRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("taxflow_prefs", Context.MODE_PRIVATE)

    // Currency State
    private val _activeCurrency = MutableStateFlow(loadSavedCurrency())
    val activeCurrency: StateFlow<AppCurrency> = _activeCurrency.asStateFlow()

    // Subscription & Trial State (Starts fresh at install time with full 30 days)
    private val _trialState = MutableStateFlow(loadTrialState())
    val trialState: StateFlow<SubscriptionTrialState> = _trialState.asStateFlow()

    // Auto-Drive background detector status
    private val _isAutoDriveActive = MutableStateFlow(prefs.getBoolean("auto_drive_active", true))
    val isAutoDriveActive: StateFlow<Boolean> = _isAutoDriveActive.asStateFlow()

    // Monthly Mileage Tally (Starts at 0.0)
    private val _monthlyLoggedKm = MutableStateFlow(prefs.getFloat("monthly_km", 0.0f).toDouble())
    val monthlyLoggedKm: StateFlow<Double> = _monthlyLoggedKm.asStateFlow()

    private val _monthlyWriteOffInr = MutableStateFlow(prefs.getFloat("monthly_writeoff", 0.0f).toDouble())
    val monthlyWriteOffInr: StateFlow<Double> = _monthlyWriteOffInr.asStateFlow()

    // Classified drives history (for Undo functionality)
    private val undoHistory = mutableListOf<MileageDrive>()

    // Unclassified Drives Queue (Clean slate: starts empty or loads saved)
    private val _unclassifiedDrives = MutableStateFlow(loadSavedDrives())
    val unclassifiedDrives: StateFlow<List<MileageDrive>> = _unclassifiedDrives.asStateFlow()

    // Expenses Stream (Clean slate: starts empty or loads saved)
    private val _expenses = MutableStateFlow(loadSavedExpenses())
    val expenses: StateFlow<List<ExpenseItem>> = _expenses.asStateFlow()

    private fun loadSavedCurrency(): AppCurrency {
        val code = prefs.getString("active_currency", AppCurrency.INR.code) ?: AppCurrency.INR.code
        return AppCurrency.values().find { it.code == code } ?: AppCurrency.INR
    }

    private fun loadTrialState(): SubscriptionTrialState {
        var installTime = prefs.getLong("install_time_millis", 0L)
        if (installTime == 0L) {
            // Fresh start from the beginning: current timestamp (Day 1 of 30, 30 days remaining)
            installTime = System.currentTimeMillis()
            prefs.edit().putLong("install_time_millis", installTime).apply()
        }
        val isPro = prefs.getBoolean("is_pro_subscriber", false)
        val isSimExpired = prefs.getBoolean("simulated_expired", false)
        return SubscriptionTrialState(
            installDateMillis = installTime,
            trialDurationDays = 30,
            isProSubscriber = isPro,
            isSimulatedExpired = isSimExpired
        )
    }

    fun setCurrency(currency: AppCurrency) {
        _activeCurrency.value = currency
        prefs.edit().putString("active_currency", currency.code).apply()
    }

    fun toggleAutoDrive() {
        val newState = !_isAutoDriveActive.value
        _isAutoDriveActive.value = newState
        prefs.edit().putBoolean("auto_drive_active", newState).apply()
    }

    fun simulateTrialExpiration(expired: Boolean) {
        prefs.edit().putBoolean("simulated_expired", expired).apply()
        _trialState.value = _trialState.value.copy(isSimulatedExpired = expired)
    }

    fun upgradeToPro() {
        prefs.edit().putBoolean("is_pro_subscriber", true).putBoolean("simulated_expired", false).apply()
        _trialState.value = _trialState.value.copy(isProSubscriber = true, isSimulatedExpired = false)
    }

    fun resetTrial() {
        val newInstall = System.currentTimeMillis()
        prefs.edit()
            .putLong("install_time_millis", newInstall)
            .putBoolean("is_pro_subscriber", false)
            .putBoolean("simulated_expired", false)
            .apply()
        _trialState.value = SubscriptionTrialState(
            installDateMillis = newInstall,
            trialDurationDays = 30,
            isProSubscriber = false,
            isSimulatedExpired = false
        )
    }

    fun clearAllData() {
        prefs.edit()
            .remove("expenses_json")
            .remove("drives_json")
            .putFloat("monthly_km", 0.0f)
            .putFloat("monthly_writeoff", 0.0f)
            .apply()
        _expenses.value = emptyList()
        _unclassifiedDrives.value = emptyList()
        _monthlyLoggedKm.value = 0.0
        _monthlyWriteOffInr.value = 0.0
        undoHistory.clear()
        resetTrial()
    }

    fun classifyTopDrive(driveId: String, classification: DriveClassification) {
        val currentList = _unclassifiedDrives.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == driveId }
        if (index != -1) {
            val drive = currentList.removeAt(index)
            val updatedDrive = drive.copy(classification = classification)
            undoHistory.add(updatedDrive)
            _unclassifiedDrives.value = currentList
            saveDrives(currentList)

            if (classification == DriveClassification.BUSINESS) {
                val newKm = _monthlyLoggedKm.value + drive.distanceKm
                val newWriteOff = _monthlyWriteOffInr.value + drive.taxWriteOffInBaseInr
                _monthlyLoggedKm.value = newKm
                _monthlyWriteOffInr.value = newWriteOff
                prefs.edit()
                    .putFloat("monthly_km", newKm.toFloat())
                    .putFloat("monthly_writeoff", newWriteOff.toFloat())
                    .apply()
            }
        }
    }

    fun undoLastClassification(): Boolean {
        if (undoHistory.isNotEmpty()) {
            val last = undoHistory.removeAt(undoHistory.lastIndex)
            if (last.classification == DriveClassification.BUSINESS) {
                val newKm = maxOf(0.0, _monthlyLoggedKm.value - last.distanceKm)
                val newWriteOff = maxOf(0.0, _monthlyWriteOffInr.value - last.taxWriteOffInBaseInr)
                _monthlyLoggedKm.value = newKm
                _monthlyWriteOffInr.value = newWriteOff
                prefs.edit()
                    .putFloat("monthly_km", newKm.toFloat())
                    .putFloat("monthly_writeoff", newWriteOff.toFloat())
                    .apply()
            }
            val restored = last.copy(classification = DriveClassification.UNCLASSIFIED)
            val updatedList = listOf(restored) + _unclassifiedDrives.value
            _unclassifiedDrives.value = updatedList
            saveDrives(updatedList)
            return true
        }
        return false
    }

    fun addExpense(expense: ExpenseItem) {
        val updated = listOf(expense) + _expenses.value
        _expenses.value = updated
        saveExpenses(updated)
    }

    fun addGeneratedTrip(start: String, dest: String, km: Double, mins: Int, aiNote: String) {
        val nextNumber = (_unclassifiedDrives.value.maxOfOrNull { it.driveNumber } ?: 100) + 1
        val newDrive = MileageDrive(
            id = "drive_${System.currentTimeMillis()}",
            driveNumber = nextNumber,
            dateText = "Drive on Today",
            timeRangeText = "Live GPS Tracked",
            startLocation = start,
            destinationLocation = dest,
            distanceKm = km,
            durationMins = mins,
            taxWriteOffInBaseInr = km * 25.0, // standard ₹25/km tax write-off
            aiSuggestion = aiNote
        )
        val updated = listOf(newDrive) + _unclassifiedDrives.value
        _unclassifiedDrives.value = updated
        saveDrives(updated)
    }

    // --- Persistence Handlers (Zero mock data, clean slate on fresh start) ---

    private fun saveExpenses(list: List<ExpenseItem>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("vendor", item.vendor)
                put("subtitle", item.subtitle)
                put("category", item.category)
                put("dateText", item.dateText)
                put("amountInBaseInr", item.amountInBaseInr)
                put("taxSavedInBaseInr", item.taxSavedInBaseInr)
                put("deductiblePercentage", item.deductiblePercentage)
                put("deductibleBadgeText", item.deductibleBadgeText)
                put("statusTagText", item.statusTagText)
                put("iconType", item.iconType.name)
                put("isNeedsReview", item.isNeedsReview)
                put("receiptNote", item.receiptNote ?: "")
            }
            array.put(obj)
        }
        prefs.edit().putString("expenses_json", array.toString()).apply()
    }

    private fun loadSavedExpenses(): List<ExpenseItem> {
        val jsonStr = prefs.getString("expenses_json", null) ?: return emptyList()
        val list = mutableListOf<ExpenseItem>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ExpenseItem(
                        id = obj.optString("id", "exp_$i"),
                        vendor = obj.optString("vendor"),
                        subtitle = obj.optString("subtitle"),
                        category = obj.optString("category"),
                        dateText = obj.optString("dateText"),
                        amountInBaseInr = obj.optDouble("amountInBaseInr"),
                        taxSavedInBaseInr = obj.optDouble("taxSavedInBaseInr"),
                        deductiblePercentage = obj.optInt("deductiblePercentage", 100),
                        deductibleBadgeText = obj.optString("deductibleBadgeText", "100% Deductible"),
                        statusTagText = obj.optString("statusTagText", "AI Verified"),
                        iconType = try {
                            ExpenseIconType.valueOf(obj.optString("iconType", ExpenseIconType.RECEIPT_GENERIC.name))
                        } catch (e: Exception) {
                            ExpenseIconType.RECEIPT_GENERIC
                        },
                        isNeedsReview = obj.optBoolean("isNeedsReview", false),
                        receiptNote = obj.optString("receiptNote").ifEmpty { null }
                    )
                )
            }
        } catch (e: Exception) {
            return emptyList()
        }
        return list
    }

    private fun saveDrives(list: List<MileageDrive>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("driveNumber", item.driveNumber)
                put("dateText", item.dateText)
                put("timeRangeText", item.timeRangeText)
                put("startLocation", item.startLocation)
                put("destinationLocation", item.destinationLocation)
                put("distanceKm", item.distanceKm)
                put("durationMins", item.durationMins)
                put("taxWriteOffInBaseInr", item.taxWriteOffInBaseInr)
                put("aiSuggestion", item.aiSuggestion)
                put("classification", item.classification.name)
            }
            array.put(obj)
        }
        prefs.edit().putString("drives_json", array.toString()).apply()
    }

    private fun loadSavedDrives(): List<MileageDrive> {
        val jsonStr = prefs.getString("drives_json", null) ?: return emptyList()
        val list = mutableListOf<MileageDrive>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    MileageDrive(
                        id = obj.optString("id", "drive_$i"),
                        driveNumber = obj.optInt("driveNumber", i + 1),
                        dateText = obj.optString("dateText"),
                        timeRangeText = obj.optString("timeRangeText"),
                        startLocation = obj.optString("startLocation"),
                        destinationLocation = obj.optString("destinationLocation"),
                        distanceKm = obj.optDouble("distanceKm"),
                        durationMins = obj.optInt("durationMins"),
                        taxWriteOffInBaseInr = obj.optDouble("taxWriteOffInBaseInr"),
                        aiSuggestion = obj.optString("aiSuggestion"),
                        classification = try {
                            DriveClassification.valueOf(obj.optString("classification", DriveClassification.UNCLASSIFIED.name))
                        } catch (e: Exception) {
                            DriveClassification.UNCLASSIFIED
                        }
                    )
                )
            }
        } catch (e: Exception) {
            return emptyList()
        }
        return list
    }
}
