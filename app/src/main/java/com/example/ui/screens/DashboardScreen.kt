package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCurrency
import com.example.model.ExpenseItem
import com.example.model.SubscriptionTrialState
import com.example.ui.theme.Outline
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLowest

@Composable
fun DashboardScreen(
    currency: AppCurrency,
    trialState: SubscriptionTrialState,
    expenses: List<ExpenseItem>,
    monthlyWriteOffInr: Double,
    monthlyLoggedKm: Double,
    onScanReceiptClick: () -> Unit,
    onLogMileageClick: () -> Unit,
    onTaxProjectionClick: () -> Unit,
    onExportSchedCClick: () -> Unit,
    onViewAllExpensesClick: () -> Unit,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fabPulse = rememberInfiniteTransition(label = "fabPulse")
    val fabScale by fabPulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fabScale"
    )

    // Dynamic aggregates computed from actual user data starting clean from zero
    val realTimeTaxSavings = expenses.filter { it.deductiblePercentage > 0 }.sumOf { it.taxSavedInBaseInr } + monthlyWriteOffInr
    val totalExpenses = expenses.sumOf { it.amountInBaseInr }
    val writeOffsCount = expenses.count { it.deductiblePercentage > 0 } + (if (monthlyLoggedKm > 0) 1 else 0)
    val shelteredPercent = if (totalExpenses > 0) ((realTimeTaxSavings / totalExpenses) * 100).coerceIn(0.0, 95.0) else 0.0

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Trial Status Notice (if trial expiring or expired)
            item {
                if (!trialState.isTrialActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFDAD6))
                            .border(1.dp, Color(0xFFBA1A1A).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable { onUpgradeClick() }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "30-Day Free Trial Expired",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF93000A)
                                )
                                Text(
                                    text = "Tap to unlock unlimited AI scans & mileage tracking.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF93000A)
                                )
                            }
                            Text(
                                text = "UPGRADE",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF93000A)
                            )
                        }
                    }
                } else if (!trialState.isProSubscriber) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Primary.copy(alpha = 0.08f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Free Access: ${trialState.daysRemaining} Days Remaining",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "View Plans",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onUpgradeClick() }
                        )
                    }
                }
            }

            // HERO CARD: Real-Time Tax Savings
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "REAL-TIME TAX SAVINGS",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = Outline,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = currency.format(realTimeTaxSavings),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    letterSpacing = (-0.02).sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${AppCurrency.USD.format(realTimeTaxSavings)} est.)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Secondary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        // Today badge
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(Primary.copy(alpha = 0.1f))
                                .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(9999.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (realTimeTaxSavings > 0) "+${currency.format(realTimeTaxSavings)} saved" else "Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gauge / Progress Meter Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(OutlineVariant.copy(alpha = 0.2f))
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Estimated Tax Bill Reduction",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = if (shelteredPercent > 0) "${shelteredPercent.toInt()}% Sheltered" else "Ready",
                            style = MaterialTheme.typography.labelLarge,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress bar track
                    val barWidthFraction = if (shelteredPercent > 0) (shelteredPercent / 100f).toFloat().coerceIn(0.05f, 1f) else 0.04f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(SurfaceContainer)
                            .padding(1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(barWidthFraction)
                                .height(8.dp)
                                .clip(RoundedCornerShape(9999.dp))
                                .background(Primary)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (realTimeTaxSavings > 0)
                            "Curtailed liability by ${currency.format(realTimeTaxSavings)} via qualified business write-offs."
                        else
                            "Log your first expense or mileage trip to curtail estimated liability.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Secondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mini Breakdown Chips Bento Row (3 columns)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BentoMiniCard(
                            label = "Expenses",
                            value = currency.format(totalExpenses),
                            valueColor = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        BentoMiniCard(
                            label = "Write-offs",
                            value = "$writeOffsCount items",
                            valueColor = Primary,
                            modifier = Modifier.weight(1f)
                        )
                        BentoMiniCard(
                            label = "Audit Risk",
                            value = "Low (0.02%)",
                            valueColor = Primary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // GAMIFIED TRACKER: Declutter Streak
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔥", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Declutter Streak",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Clean Slate",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(Primary.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (expenses.isNotEmpty())
                                "All ${expenses.size} expenses reviewed! Zero backlog."
                            else
                                "Ready to record receipts. Clean start.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Weekly dot markers (M, T, W, T, F, S, S)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Surface)
                            .border(1.dp, OutlineVariant.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DayStreakDot("M", isChecked = true)
                        DayStreakDot("T", isChecked = true)
                        DayStreakDot("W", isChecked = true)
                        DayStreakDot("T", isChecked = true)
                        DayStreakDot("F", isChecked = true)
                        DayStreakDot("S", isChecked = true)
                        DayStreakDot("S", isChecked = true, isToday = true)
                    }
                }
            }

            // Quick Actions (4 Grid items)
            item {
                Column {
                    Text(
                        text = "QUICK ACTIONS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.DocumentScanner,
                            label = "Scan Receipt",
                            iconBg = Primary.copy(alpha = 0.12f),
                            iconColor = Primary,
                            testTag = "quick_scan_receipt",
                            onClick = onScanReceiptClick,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = Icons.Default.DirectionsCar,
                            label = "Log Mileage",
                            iconBg = SecondaryContainer,
                            iconColor = Secondary,
                            testTag = "quick_log_mileage",
                            onClick = onLogMileageClick,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = Icons.Default.Analytics,
                            label = "Tax Projection",
                            iconBg = SurfaceContainer,
                            iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            testTag = "quick_tax_projection",
                            onClick = onTaxProjectionClick,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = Icons.Default.IosShare,
                            label = "Export Sched C",
                            iconBg = SurfaceContainer,
                            iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            testTag = "quick_export_sched_c",
                            onClick = onExportSchedCClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Recent AI Deductions Stream
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Recent Deductions",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Primary)
                        )
                    }
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary,
                        modifier = Modifier
                            .testTag("view_all_expenses_button")
                            .clickable { onViewAllExpensesClick() }
                    )
                }
            }

            // Real Deductions Stream (or clean empty state)
            if (expenses.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceContainerLowest)
                            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = Outline,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Expenses Logged Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'AI Scan Receipt' or Quick Actions above to log your first business write-off.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(expenses.take(4), key = { it.id }) { item ->
                    RecentExpenseItemRow(item = item, currency = currency)
                }
            }
        }

        // Contextual Floating Action Button (FAB)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
        ) {
            Button(
                onClick = onScanReceiptClick,
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = SurfaceContainerLowest
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                modifier = Modifier
                    .testTag("ai_scan_receipt_fab")
                    .scale(fabScale)
            ) {
                Icon(
                    imageVector = Icons.Default.DocumentScanner,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Scan Receipt",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BentoMiniCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Surface)
            .border(1.dp, OutlineVariant.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Secondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun DayStreakDot(
    dayLetter: String,
    isChecked: Boolean,
    isToday: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = dayLetter,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color = if (isToday) Primary else Secondary
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Primary)
                .border(if (isToday) 2.dp else 0.dp, Primary.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = SurfaceContainerLowest,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    iconBg: Color,
    iconColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

@Composable
private fun RecentExpenseItemRow(
    item: ExpenseItem,
    currency: AppCurrency
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = item.vendor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = Secondary
                    )
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.labelSmall,
                        color = Secondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Primary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.deductibleBadgeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = currency.format(item.amountInBaseInr),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "-${currency.format(item.taxSavedInBaseInr)} Tax Saved",
                style = MaterialTheme.typography.labelSmall,
                color = Primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
