package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.DashboardHeader
import com.example.ui.components.ManualAddExpenseDialog
import com.example.ui.components.PaywallDialog
import com.example.ui.components.ReceiptScanDialog
import com.example.ui.components.SystemStatusBar
import com.example.ui.components.TaxFlowBottomNavBar
import com.example.ui.components.TaxProjectionDialog
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.MileageScreen
import com.example.ui.theme.Surface
import com.example.ui.theme.TaxFlowTheme
import com.example.viewmodel.ScreenTab
import com.example.viewmodel.TaxFlowViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaxFlowTheme {
                TaxFlowApp()
            }
        }
    }
}

@Composable
fun TaxFlowApp(
    viewModel: TaxFlowViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeCurrency by viewModel.activeCurrency.collectAsStateWithLifecycle()
    val trialState by viewModel.trialState.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val filteredExpenses by viewModel.filteredExpenses.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilterChip by viewModel.selectedFilterChip.collectAsStateWithLifecycle()
    val isAutoDriveActive by viewModel.isAutoDriveActive.collectAsStateWithLifecycle()
    val unclassifiedDrives by viewModel.unclassifiedDrives.collectAsStateWithLifecycle()
    val monthlyLoggedKm by viewModel.monthlyLoggedKm.collectAsStateWithLifecycle()
    val monthlyWriteOffInr by viewModel.monthlyWriteOffInr.collectAsStateWithLifecycle()

    val showPaywall by viewModel.showPaywall.collectAsStateWithLifecycle()
    val paywallReason by viewModel.paywallReason.collectAsStateWithLifecycle()
    val showScanDialog by viewModel.showScanDialog.collectAsStateWithLifecycle()
    val showManualAddDialog by viewModel.showManualAddDialog.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val scanResult by viewModel.scanResult.collectAsStateWithLifecycle()
    val showTaxProjectionDialog by viewModel.showTaxProjectionDialog.collectAsStateWithLifecycle()

    // Handle Toast Events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect {
            // Observed in Compose runtime
        }
    }

    // Back button handling: Return to Dashboard if on sub-screens
    if (currentTab != ScreenTab.DASHBOARD) {
        BackHandler {
            viewModel.selectTab(ScreenTab.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            TaxFlowBottomNavBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Mobile status bar (9:41, icons)
            SystemStatusBar()

            // Header matching current tab
            when (currentTab) {
                ScreenTab.DASHBOARD -> {
                    DashboardHeader(
                        onProfileClick = { viewModel.selectTab(ScreenTab.ACCOUNT) },
                        onNotificationsClick = { viewModel.selectTab(ScreenTab.EXPENSES) },
                        onWalletClick = { viewModel.openTaxProjectionDialog() }
                    )
                }
                ScreenTab.EXPENSES -> {
                    AppHeader(
                        title = "TaxFlow",
                        subtitle = null,
                        onNotificationsClick = { },
                        onMoreClick = { viewModel.openTaxProjectionDialog() }
                    )
                }
                ScreenTab.MILEAGE -> {
                    AppHeader(
                        title = "TaxFlow",
                        subtitle = "Mileage Ledger",
                        onNotificationsClick = { },
                        onMoreClick = { viewModel.simulateNewDrive() }
                    )
                }
                ScreenTab.ACCOUNT -> {
                    AppHeader(
                        title = "Account & Settings",
                        subtitle = "TaxFlow Pro Ledger",
                        onNotificationsClick = { },
                        onMoreClick = { }
                    )
                }
            }

            // Screen Body
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    ScreenTab.DASHBOARD -> {
                        DashboardScreen(
                            currency = activeCurrency,
                            trialState = trialState,
                            expenses = allExpenses,
                            monthlyWriteOffInr = monthlyWriteOffInr,
                            monthlyLoggedKm = monthlyLoggedKm,
                            onScanReceiptClick = { viewModel.checkAndExecuteAiScan() },
                            onLogMileageClick = { viewModel.selectTab(ScreenTab.MILEAGE) },
                            onTaxProjectionClick = { viewModel.openTaxProjectionDialog() },
                            onExportSchedCClick = { viewModel.openTaxProjectionDialog() },
                            onViewAllExpensesClick = { viewModel.selectTab(ScreenTab.EXPENSES) },
                            onUpgradeClick = { viewModel.openDirectUpgrade() }
                        )
                    }
                    ScreenTab.EXPENSES -> {
                        ExpensesScreen(
                            currency = activeCurrency,
                            expenses = filteredExpenses,
                            searchQuery = searchQuery,
                            selectedFilterChip = selectedFilterChip,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onFilterChipSelect = { viewModel.setSelectedFilterChip(it) },
                            onCurrencySelect = { viewModel.setCurrency(it) },
                            onAddExpenseClick = { viewModel.openManualAddDialog() },
                            onExportClick = { viewModel.openTaxProjectionDialog() }
                        )
                    }
                    ScreenTab.MILEAGE -> {
                        MileageScreen(
                            currency = activeCurrency,
                            isAutoDriveActive = isAutoDriveActive,
                            unclassifiedDrives = unclassifiedDrives,
                            monthlyLoggedKm = monthlyLoggedKm,
                            monthlyWriteOffInr = monthlyWriteOffInr,
                            onToggleAutoDrive = { viewModel.toggleAutoDrive() },
                            onClassifyDrive = { id, classification ->
                                viewModel.checkAndClassifyDrive(id, classification)
                            },
                            onUndoLastClassification = { viewModel.undoLastDriveClassification() },
                            onSimulateTripClick = { viewModel.simulateNewDrive() }
                        )
                    }
                    ScreenTab.ACCOUNT -> {
                        AccountScreen(
                            currency = activeCurrency,
                            trialState = trialState,
                            onCurrencySelect = { viewModel.setCurrency(it) },
                            onUpgradeClick = { viewModel.openDirectUpgrade() },
                            onSimulateExpiredToggle = { viewModel.toggleSimulatedExpiration(it) },
                            onResetTrialClick = { viewModel.resetTrial() },
                            onExportClick = { viewModel.openTaxProjectionDialog() },
                            onClearDataClick = { viewModel.clearAllData() }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showPaywall) {
        PaywallDialog(
            reasonMessage = paywallReason,
            currency = activeCurrency,
            onDismiss = { viewModel.dismissPaywall() },
            onUpgrade = { viewModel.upgradeToPro() },
            onResetTrial = { viewModel.resetTrial() }
        )
    }

    if (showScanDialog) {
        ReceiptScanDialog(
            isScanning = isScanning,
            scanResult = scanResult,
            currency = activeCurrency,
            onClose = { viewModel.closeScanDialog() },
            onStartScan = { customText -> viewModel.processReceipt(null, customText) },
            onConfirmExpense = { viewModel.confirmAndSaveScannedExpense() }
        )
    }

    if (showManualAddDialog) {
        ManualAddExpenseDialog(
            currency = activeCurrency,
            onDismiss = { viewModel.closeManualAddDialog() },
            onSaveExpense = { vendor, amount, category, deductiblePercentage, note ->
                viewModel.addManualExpense(vendor, amount, category, deductiblePercentage, note)
            }
        )
    }

    if (showTaxProjectionDialog) {
        TaxProjectionDialog(
            currency = activeCurrency,
            onClose = { viewModel.closeTaxProjectionDialog() }
        )
    }
}
