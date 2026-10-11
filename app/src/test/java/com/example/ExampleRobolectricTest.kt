package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GeminiReceiptScanner
import com.example.data.TaxFlowRepository
import com.example.model.AppCurrency
import com.example.model.DriveClassification
import com.example.model.ExpenseIconType
import com.example.model.ExpenseItem
import com.example.model.SubscriptionTrialState
import com.example.viewmodel.ScreenTab
import com.example.viewmodel.TaxFlowViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies app identity`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TaxFlow", appName)
    }

    @Test
    fun `fresh repository starts clean from the beginning without test data`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = TaxFlowRepository(context)
        repository.clearAllData()

        // Clean slate checks
        assertEquals(0, repository.expenses.value.size)
        assertEquals(0, repository.unclassifiedDrives.value.size)
        assertEquals(0.0, repository.monthlyLoggedKm.value, 0.001)
        assertEquals(0.0, repository.monthlyWriteOffInr.value, 0.001)
        assertEquals(30, repository.trialState.value.daysRemaining)
        assertTrue(repository.trialState.value.isTrialActive)
    }

    @Test
    fun `currency formatting converts amounts across all supported currencies`() {
        val baseInr = 142850.0

        // INR format with Indian thousand separators
        val inrFormatted = AppCurrency.INR.format(baseInr)
        assertTrue(inrFormatted.contains("₹"))
        assertTrue(inrFormatted.contains("1,42,850"))

        // USD format
        val usdFormatted = AppCurrency.USD.format(baseInr)
        assertTrue(usdFormatted.contains("$"))

        // EUR format
        val eurFormatted = AppCurrency.EUR.format(baseInr)
        assertTrue(eurFormatted.contains("€"))

        // GBP format
        val gbpFormatted = AppCurrency.GBP.format(baseInr)
        assertTrue(gbpFormatted.contains("£"))

        // JPY format
        val jpyFormatted = AppCurrency.JPY.format(baseInr)
        assertTrue(jpyFormatted.contains("¥"))
    }

    @Test
    fun `time-bound trial logic accurately handles day counters and expiration lockdown`() {
        // Fresh trial on install day (30 days remaining)
        val freshTrial = SubscriptionTrialState(
            installDateMillis = System.currentTimeMillis(),
            trialDurationDays = 30,
            isProSubscriber = false,
            isSimulatedExpired = false
        )
        assertTrue(freshTrial.isTrialActive)
        assertTrue(freshTrial.canUseAiScanning)
        assertTrue(freshTrial.canLogUnlimitedMileage)
        assertEquals(30, freshTrial.daysRemaining)

        // Day 30: exactly 0 days left
        val day30Trial = SubscriptionTrialState(
            installDateMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30),
            trialDurationDays = 30,
            isProSubscriber = false,
            isSimulatedExpired = false
        )
        assertFalse(day30Trial.isTrialActive)
        assertFalse(day30Trial.canUseAiScanning)
        assertFalse(day30Trial.canLogUnlimitedMileage)
        assertEquals(0, day30Trial.daysRemaining)

        // Day 31: Expired trial
        val expiredTrial = SubscriptionTrialState(
            installDateMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(31),
            trialDurationDays = 30,
            isProSubscriber = false,
            isSimulatedExpired = false
        )
        assertFalse(expiredTrial.isTrialActive)
        assertFalse(expiredTrial.canUseAiScanning)
        assertFalse(expiredTrial.canLogUnlimitedMileage)
        assertEquals(0, expiredTrial.daysRemaining)

        // Simulated Expired override toggle
        val simExpiredTrial = freshTrial.copy(isSimulatedExpired = true)
        assertFalse(simExpiredTrial.isTrialActive)
        assertFalse(simExpiredTrial.canUseAiScanning)

        // Pro subscriber bypasses expiration
        val proState = expiredTrial.copy(isProSubscriber = true)
        assertTrue(proState.isTrialActive)
        assertTrue(proState.canUseAiScanning)
        assertTrue(proState.canLogUnlimitedMileage)
    }

    @Test
    fun `mileage repository classifies newly added drives and supports undo`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = TaxFlowRepository(context)
        repository.clearAllData()

        assertEquals(0, repository.unclassifiedDrives.value.size)

        // Add a new trip from the beginning
        repository.addGeneratedTrip(
            start = "Home Office",
            dest = "Client HQ",
            km = 20.0,
            mins = 35,
            aiNote = "Business Client Meeting"
        )
        assertEquals(1, repository.unclassifiedDrives.value.size)

        val drive = repository.unclassifiedDrives.value.first()
        assertEquals(20.0, drive.distanceKm, 0.01)

        // Classify as business write-off
        repository.classifyTopDrive(drive.id, DriveClassification.BUSINESS)
        assertEquals(0, repository.unclassifiedDrives.value.size)
        assertEquals(20.0, repository.monthlyLoggedKm.value, 0.01)
        assertEquals(500.0, repository.monthlyWriteOffInr.value, 0.01)

        // Undo classification
        val undoSuccess = repository.undoLastClassification()
        assertTrue(undoSuccess)
        assertEquals(1, repository.unclassifiedDrives.value.size)
        assertEquals(0.0, repository.monthlyLoggedKm.value, 0.01)
        assertEquals(0.0, repository.monthlyWriteOffInr.value, 0.01)
    }

    @Test
    fun `gemini receipt scanner parses sample text and extracts deductible fields`() = runBlocking {
        val scanner = GeminiReceiptScanner()

        val sampleText = """
            Third Wave Coffee Koramangala
            Tax Invoice #9281
            Date: Oct 26, 2024
            Client Meeting with Dev Lead
            Total: ₹850.00
        """.trimIndent()

        val result = scanner.parseReceipt(null, sampleText)

        assertNotNull(result)
        assertTrue(result.vendor.contains("Third Wave Coffee", ignoreCase = true))
        assertEquals(850.0, result.totalAmount, 0.01)
        assertEquals(50, result.deductiblePercentage) // Client meals 50%
        assertTrue(result.taxSavingsEst > 0)
    }

    @Test
    fun `viewModel handles manual expense logging and dynamic filtering from clean slate`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = TaxFlowViewModel(app)
        viewModel.clearAllData()

        assertEquals(0, viewModel.allExpenses.value.size)

        // Add first expense from beginning
        viewModel.addManualExpense(
            vendor = "Adobe Creative Cloud",
            amount = 4000.0,
            category = "Software & SaaS",
            deductiblePercentage = 100,
            note = "Design suite subscription"
        )
        assertEquals(1, viewModel.allExpenses.value.size)

        // Add second expense
        viewModel.addManualExpense(
            vendor = "Client Dinner Cafe",
            amount = 1200.0,
            category = "Client Meals",
            deductiblePercentage = 50,
            note = "Q4 review meeting"
        )
        assertEquals(2, viewModel.allExpenses.value.size)

        // Search query filtering
        viewModel.setSearchQuery("Adobe")
        assertEquals(1, viewModel.filteredExpenses.value.size)
        assertEquals("Adobe Creative Cloud", viewModel.filteredExpenses.value.first().vendor)

        // Clear search
        viewModel.setSearchQuery("")
        assertEquals(2, viewModel.filteredExpenses.value.size)

        // Filter by 100% Deductible
        viewModel.setSelectedFilterChip("100% Deductible")
        assertEquals(1, viewModel.filteredExpenses.value.size)

        // Filter by Meals (50%)
        viewModel.setSelectedFilterChip("Meals (50%)")
        assertEquals(1, viewModel.filteredExpenses.value.size)
    }

    @Test
    fun `viewModel locks features and displays paywall when trial expires`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = TaxFlowViewModel(app)
        viewModel.clearAllData()

        // Simulate expired trial
        viewModel.toggleSimulatedExpiration(true)
        assertFalse(viewModel.trialState.value.isTrialActive)

        // Trigger AI scan -> Must open paywall dialog
        viewModel.checkAndExecuteAiScan()
        assertTrue(viewModel.showPaywall.value)
        assertFalse(viewModel.showScanDialog.value)

        // Dismiss paywall
        viewModel.dismissPaywall()
        assertFalse(viewModel.showPaywall.value)

        // Add a trip and try to classify when trial expired
        viewModel.simulateNewDrive()
        val driveId = viewModel.unclassifiedDrives.value.first().id
        viewModel.checkAndClassifyDrive(driveId, DriveClassification.BUSINESS)
        assertTrue(viewModel.showPaywall.value)

        // Upgrade to Pro -> Closes paywall and unblocks features
        viewModel.upgradeToPro()
        assertFalse(viewModel.showPaywall.value)
        assertTrue(viewModel.trialState.value.isProSubscriber)
        assertTrue(viewModel.trialState.value.canUseAiScanning)

        // Trigger AI scan now works
        viewModel.checkAndExecuteAiScan()
        assertTrue(viewModel.showScanDialog.value)
        viewModel.closeScanDialog()
    }

    @Test
    fun `viewModel tab selection navigation works smoothly`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = TaxFlowViewModel(app)

        assertEquals(ScreenTab.DASHBOARD, viewModel.currentTab.value)

        viewModel.selectTab(ScreenTab.EXPENSES)
        assertEquals(ScreenTab.EXPENSES, viewModel.currentTab.value)

        viewModel.selectTab(ScreenTab.MILEAGE)
        assertEquals(ScreenTab.MILEAGE, viewModel.currentTab.value)

        viewModel.selectTab(ScreenTab.ACCOUNT)
        assertEquals(ScreenTab.ACCOUNT, viewModel.currentTab.value)

        viewModel.selectTab(ScreenTab.DASHBOARD)
        assertEquals(ScreenTab.DASHBOARD, viewModel.currentTab.value)
    }

    @Test
    fun `viewModel clear all data wipes state cleanly`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = TaxFlowViewModel(app)

        // Add some data
        viewModel.addManualExpense("Test Item", 500.0, "Supplies", 100)
        viewModel.simulateNewDrive()
        assertTrue(viewModel.allExpenses.value.isNotEmpty())
        assertTrue(viewModel.unclassifiedDrives.value.isNotEmpty())

        // Clear all data
        viewModel.clearAllData()
        assertEquals(0, viewModel.allExpenses.value.size)
        assertEquals(0, viewModel.unclassifiedDrives.value.size)
        assertEquals(0.0, viewModel.monthlyLoggedKm.value, 0.001)
        assertEquals(0.0, viewModel.monthlyWriteOffInr.value, 0.001)
    }
}
