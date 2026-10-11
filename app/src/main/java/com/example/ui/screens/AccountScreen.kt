package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCurrency
import com.example.model.SubscriptionTrialState
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLowest

@Composable
fun AccountScreen(
    currency: AppCurrency,
    trialState: SubscriptionTrialState,
    onCurrencySelect: (AppCurrency) -> Unit,
    onUpgradeClick: () -> Unit,
    onSimulateExpiredToggle: (Boolean) -> Unit,
    onResetTrialClick: () -> Unit,
    onExportClick: () -> Unit,
    onClearDataClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineVariant.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👨‍💻",
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Alex Rivera",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "alex.rivera@designstudio.io",
                        style = MaterialTheme.typography.bodySmall,
                        color = Secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(Primary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Self-Employed • 30% Tax Bracket",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // TRIAL & SUBSCRIPTION CARD
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineVariant.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Membership & Trial",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = trialState.statusBadgeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (trialState.isTrialActive) Primary else Color(0xFFBA1A1A),
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(if (trialState.isTrialActive) Primary.copy(alpha = 0.12f) else Color(0xFFFFDAD6))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (!trialState.isProSubscriber) {
                    val progress = (trialState.daysRemaining / 30f).coerceIn(0f, 1f)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "1-Month Free Access Period",
                                style = MaterialTheme.typography.labelSmall,
                                color = Secondary
                            )
                            Text(
                                text = "${trialState.daysRemaining} days left",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(9999.dp))
                                .background(SurfaceContainer)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(if (trialState.daysRemaining > 0) Primary else Color(0xFFBA1A1A))
                            )
                        }
                    }

                    Button(
                        onClick = onUpgradeClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("account_upgrade_button"),
                        shape = RoundedCornerShape(9999.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (trialState.isTrialActive) "Upgrade to TaxFlow Pro (Save 35%)" else "Unlock Pro Subscription",
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Primary.copy(alpha = 0.08f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TaxFlow Pro Lifetime Member • All Capabilities Active",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }
            }
        }

        // TIME-BOUND TRIAL TESTING CONTROLS (User Requirement 1)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineVariant.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Trial Verification & Testing Controls",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Verify Requirement 1: Exactly 30 days post-activation, core capabilities (AI scanning, unlimited mileage) lock down and redirect user to paywall.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Secondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Simulate 30-Day Expired State",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Locks AI scanning & mileage logs immediately",
                            style = MaterialTheme.typography.bodySmall,
                            color = Secondary
                        )
                    }
                    Switch(
                        checked = trialState.isSimulatedExpired,
                        onCheckedChange = { onSimulateExpiredToggle(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Primary,
                            checkedTrackColor = Primary.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("simulate_expiry_toggle")
                    )
                }

                OutlinedButton(
                    onClick = onResetTrialClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(9999.dp)
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset Trial to 30 Days (Active)", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // GLOBAL MULTI-CURRENCY LOGIC (User Requirement 2)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineVariant.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CurrencyExchange,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Global Multi-Currency Ledger",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Controls active currency workspace. Converts all list entities, expense aggregates, and tax projections instantly using local conversion modifiers.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Secondary
                )

                // Currency Grid
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppCurrency.values().forEach { curr ->
                        val isSelected = currency == curr
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Primary.copy(alpha = 0.1f) else Surface)
                                .border(1.dp, if (isSelected) Primary else OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .clickable { onCurrencySelect(curr) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = curr.flag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${curr.name} (${curr.symbol})",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = curr.countryComplianceNote,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Secondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Text(
                                text = if (curr == AppCurrency.INR) "1.00 (Base)" else "${curr.rateFromInr}x",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Primary else Secondary
                            )
                        }
                    }
                }
            }
        }

        // Export & Data
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineVariant.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "COMPLIANCE & EXPORTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Secondary,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onExportClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Export Tax Ledger (Schedule C PDF/CSV)", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = onClearDataClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("clear_all_data_button"),
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFBA1A1A))
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, tint = Color(0xFFBA1A1A), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clear All Data (Start Clean From Scratch)", fontWeight = FontWeight.SemiBold, color = Color(0xFFBA1A1A))
                }
            }
        }
    }
}
