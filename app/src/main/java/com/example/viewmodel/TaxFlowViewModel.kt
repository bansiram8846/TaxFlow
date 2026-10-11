package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeminiReceiptScanner
import com.example.data.ReceiptScanResult
import com.example.data.TaxFlowRepository
import com.example.model.AppCurrency
import com.example.model.DriveClassification
import com.example.model.ExpenseIconType
import com.example.model.ExpenseItem
import com.example.model.MileageDrive
import com.example.model.SubscriptionTrialState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    DASHBOARD,
    EXPENSES,
    MILEAGE,
    ACCOUNT
}

class TaxFlowViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaxFlowRepository(application.applicationContext)
    private val scanner = GeminiReceiptScanner()

    val activeCurrency: StateFlow<AppCurrency> = repository.activeCurrency
    val trialState: StateFlow<SubscriptionTrialState> = repository.trialState
    val isAutoDriveActive: StateFlow<Boolean> = repository.isAutoDriveActive
    val unclassifiedDrives: StateFlow<List<MileageDrive>> = repository.unclassifiedDrives
    val monthlyLoggedKm: StateFlow<Double> = repository.monthlyLoggedKm
    val monthlyWriteOffInr: StateFlow<Double> = repository.monthlyWriteOffInr

    // Active Screen Tab
    private val _currentTab = MutableStateFlow(ScreenTab.DASHBOARD)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    // Expenses Search & Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilterChip = MutableStateFlow("All")
    val selectedFilterChip: StateFlow<String> = _selectedFilterChip.asStateFlow()

    // Filtered Expenses (Instant Synchronous State)
    private val _filteredExpenses = MutableStateFlow(repository.expenses.value)
    val filteredExpenses: StateFlow<List<ExpenseItem>> = _filteredExpenses.asStateFlow()

    init {
        viewModelScope.launch {
            repository.expenses.collect {
                updateFilteredExpenses()
            }
        }
    }

    private fun updateFilteredExpenses() {
        val query = _searchQuery.value
        val chip = _selectedFilterChip.value
        _filteredExpenses.value = repository.expenses.value.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.vendor.contains(query, ignoreCase = true) ||
                    item.subtitle.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)

            val matchesChip = when (chip) {
                "All" -> true
                "100% Deductible" -> item.deductiblePercentage == 100
                "Needs Review" -> item.isNeedsReview
                "Meals (50%)" -> item.category.contains("Meals", ignoreCase = true) || item.deductiblePercentage == 50
                "Software & SaaS" -> item.category.contains("Software", ignoreCase = true) || item.category.contains("SaaS", ignoreCase = true)
                "Travel" -> item.category.contains("Travel", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesChip
        }
    }

    // All expenses raw
    val allExpenses: StateFlow<List<ExpenseItem>> = repository.expenses

    // Dialog & Flow States
    private val _showPaywall = MutableStateFlow(false)
    val showPaywall: StateFlow<Boolean> = _showPaywall.asStateFlow()

    private val _paywallReason = MutableStateFlow("trial_expired")
    val paywallReason: StateFlow<String> = _paywallReason.asStateFlow()

    private val _showScanDialog = MutableStateFlow(false)
    val showScanDialog: StateFlow<Boolean> = _showScanDialog.asStateFlow()

    private val _showManualAddDialog = MutableStateFlow(false)
    val showManualAddDialog: StateFlow<Boolean> = _showManualAddDialog.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanResult = MutableStateFlow<ReceiptScanResult?>(null)
    val scanResult: StateFlow<ReceiptScanResult?> = _scanResult.asStateFlow()

    private val _showTaxProjectionDialog = MutableStateFlow(false)
    val showTaxProjectionDialog: StateFlow<Boolean> = _showTaxProjectionDialog.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    fun selectTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun setCurrency(currency: AppCurrency) {
        repository.setCurrency(currency)
        viewModelScope.launch {
            _toastEvent.emit("Active ledger updated to ${currency.code} (${currency.symbol})")
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        updateFilteredExpenses()
    }

    fun setSelectedFilterChip(chip: String) {
        _selectedFilterChip.value = chip
        updateFilteredExpenses()
    }

    fun toggleAutoDrive() {
        repository.toggleAutoDrive()
        val newState = !isAutoDriveActive.value
        viewModelScope.launch {
            _toastEvent.emit(if (newState) "Background GPS Auto-Drive Enabled" else "Auto-Drive Paused")
        }
    }

    // Trial verification before premium features
    fun checkAndExecuteAiScan() {
        val trial = trialState.value
        if (!trial.canUseAiScanning) {
            _paywallReason.value = "Automated AI receipt scanning is a Pro capability. Your 30-day free trial has expired."
            _showPaywall.value = true
        } else {
            _scanResult.value = null
            _showScanDialog.value = true
        }
    }

    fun checkAndClassifyDrive(driveId: String, classification: DriveClassification) {
        val trial = trialState.value
        if (!trial.canLogUnlimitedMileage) {
            _paywallReason.value = "Unlimited automatic mileage write-off logs is a Pro feature. Your 30-day free trial has expired."
            _showPaywall.value = true
            return
        }
        repository.classifyTopDrive(driveId, classification)
        viewModelScope.launch {
            if (classification == DriveClassification.BUSINESS) {
                _toastEvent.emit("Drive classified as Business Write-off (+Tax savings recorded)")
            } else {
                _toastEvent.emit("Drive marked as Personal")
            }
        }
    }

    fun undoLastDriveClassification() {
        val undone = repository.undoLastClassification()
        if (undone) {
            viewModelScope.launch {
                _toastEvent.emit("Last drive decision undone")
            }
        }
    }

    fun dismissPaywall() {
        _showPaywall.value = false
    }

    fun openDirectUpgrade() {
        _paywallReason.value = "Unlock TaxFlow Pro for unlimited AI receipt processing, background GPS mileage logs, and Schedule C exports."
        _showPaywall.value = true
    }

    fun upgradeToPro() {
        repository.upgradeToPro()
        _showPaywall.value = false
        viewModelScope.launch {
            _toastEvent.emit("Welcome to TaxFlow Pro! All features unlocked.")
        }
    }

    fun toggleSimulatedExpiration(expired: Boolean) {
        repository.simulateTrialExpiration(expired)
        viewModelScope.launch {
            _toastEvent.emit(if (expired) "Trial simulated as Expired (30+ days passed)" else "Active trial status restored")
        }
    }

    fun resetTrial() {
        repository.resetTrial()
        viewModelScope.launch {
            _toastEvent.emit("Trial timer reset to Day 1 (30 days remaining)")
        }
    }

    fun closeScanDialog() {
        _showScanDialog.value = false
        _scanResult.value = null
        _isScanning.value = false
    }

    fun processReceipt(bitmap: Bitmap?, customNotes: String? = null) {
        viewModelScope.launch {
            _isScanning.value = true
            val result = scanner.parseReceipt(bitmap, customNotes)
            _scanResult.value = result
            _isScanning.value = false
        }
    }

    fun confirmAndSaveScannedExpense() {
        val res = _scanResult.value ?: return
        val newExpense = ExpenseItem(
            id = "exp_${System.currentTimeMillis()}",
            vendor = res.vendor,
            subtitle = "${res.category} • Scanned via AI",
            category = res.category,
            dateText = res.dateText,
            amountInBaseInr = res.totalAmount,
            taxSavedInBaseInr = res.taxSavingsEst,
            deductiblePercentage = res.deductiblePercentage,
            deductibleBadgeText = "${res.deductiblePercentage}% Deductible",
            statusTagText = "AI Verified",
            iconType = when {
                res.category.contains("Meal", true) -> ExpenseIconType.CAFE
                res.category.contains("Software", true) -> ExpenseIconType.DESIGN
                res.category.contains("Travel", true) -> ExpenseIconType.CAR
                res.category.contains("Equip", true) -> ExpenseIconType.EQUIPMENT
                else -> ExpenseIconType.RECEIPT_GENERIC
            },
            receiptNote = res.aiNote
        )
        repository.addExpense(newExpense)
        closeScanDialog()
        viewModelScope.launch {
            _toastEvent.emit("Added ${res.vendor} (${res.totalAmount}) to active ledger!")
        }
    }

    fun openTaxProjectionDialog() {
        _showTaxProjectionDialog.value = true
    }

    fun closeTaxProjectionDialog() {
        _showTaxProjectionDialog.value = false
    }

    fun openManualAddDialog() {
        _showManualAddDialog.value = true
    }

    fun closeManualAddDialog() {
        _showManualAddDialog.value = false
    }

    fun addManualExpense(
        vendor: String,
        amount: Double,
        category: String,
        deductiblePercentage: Int,
        note: String? = null
    ) {
        val taxSaved = amount * (deductiblePercentage / 100.0) * 0.30
        val newExpense = ExpenseItem(
            id = "exp_${System.currentTimeMillis()}",
            vendor = vendor.ifBlank { "Custom Expense" },
            subtitle = "$category • Logged",
            category = category,
            dateText = "Today",
            amountInBaseInr = amount,
            taxSavedInBaseInr = taxSaved,
            deductiblePercentage = deductiblePercentage,
            deductibleBadgeText = "$deductiblePercentage% Deductible",
            statusTagText = "Verified",
            iconType = when {
                category.contains("Meal", true) -> ExpenseIconType.CAFE
                category.contains("Software", true) -> ExpenseIconType.DESIGN
                category.contains("Travel", true) -> ExpenseIconType.CAR
                category.contains("Chair", true) || category.contains("Office", true) -> ExpenseIconType.CHAIR
                else -> ExpenseIconType.RECEIPT_GENERIC
            },
            receiptNote = note
        )
        repository.addExpense(newExpense)
        updateFilteredExpenses()
        closeManualAddDialog()
        viewModelScope.launch {
            _toastEvent.emit("Added $vendor to ledger")
        }
    }

    fun clearAllData() {
        repository.clearAllData()
        updateFilteredExpenses()
        viewModelScope.launch {
            _toastEvent.emit("Reset all data to beginning. Clean ledger ready.")
        }
    }

    fun simulateNewDrive() {
        val sampleRoutes = listOf(
            Triple("Indiranagar Studio", "Koramangala Sony World", 14.2),
            Triple("Client Office", "Manyata Embassy Business Park", 19.5),
            Triple("Co-working space", "Bangalore Int'l Exhibition Centre", 27.8)
        )
        val selected = sampleRoutes.random()
        repository.addGeneratedTrip(
            start = selected.first,
            dest = selected.second,
            km = selected.third,
            mins = (selected.third * 1.8).toInt(),
            aiNote = "AI suggests Business: Recurring client destination route detected."
        )
        viewModelScope.launch {
            _toastEvent.emit("Auto-Drive: Detected new trip (${selected.third} km)")
        }
    }
}
