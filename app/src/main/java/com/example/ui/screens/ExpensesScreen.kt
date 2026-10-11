package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCurrency
import com.example.model.ExpenseIconType
import com.example.model.ExpenseItem
import com.example.ui.theme.Outline
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest

@Composable
fun ExpensesScreen(
    currency: AppCurrency,
    expenses: List<ExpenseItem>,
    searchQuery: String,
    selectedFilterChip: String,
    onSearchChange: (String) -> Unit,
    onFilterChipSelect: (String) -> Unit,
    onCurrencySelect: (AppCurrency) -> Unit,
    onAddExpenseClick: () -> Unit,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalQ4Deductible = expenses.filter { it.deductiblePercentage > 0 }.sumOf { it.taxSavedInBaseInr }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Screen Header & Item Counter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Expenses & Transactions",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-0.02).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Primary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Auto-categorization active • ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${expenses.size} Items",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                    }

                    // Sync Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .clickable { }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Sync",
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sync",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    }
                }
            }

            // MULTI-CURRENCY SWITCHER (Segmented Pill Bar)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerLow)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppCurrency.values().forEach { curr ->
                        val isSelected = currency == curr
                        val bgColor by animateColorAsState(
                            targetValue = if (isSelected) Primary else Color.Transparent,
                            label = "currBg"
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            label = "currText"
                        )

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .clickable { onCurrencySelect(curr) }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = curr.pillLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = textColor,
                                fontSize = 11.sp
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // CURRENCY CONVERSION / TAX COMPLIANCE NOTICE
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CurrencyRupee,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Active Ledger: ${currency.name} (${currency.symbol}) • ${currency.countryComplianceNote}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "Live FX",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            // Search & Filter Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchChange,
                            placeholder = {
                                Text(
                                    "Search vendor, category, or note...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Outline
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("expense_search_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SurfaceContainerLowest,
                                unfocusedContainerColor = SurfaceContainerLowest,
                                focusedIndicatorColor = Primary,
                                unfocusedIndicatorColor = OutlineVariant.copy(alpha = 0.4f)
                            )
                        )

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerLowest)
                                .border(1.dp, OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .clickable { }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Tune Filter",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Scrolling Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filterOptions = listOf(
                            "All",
                            "100% Deductible",
                            "Needs Review",
                            "Meals (50%)",
                            "Software & SaaS",
                            "Travel"
                        )
                        filterOptions.forEach { filter ->
                            val isSelected = selectedFilterChip == filter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(if (isSelected) Primary else SurfaceContainerLowest)
                                    .border(
                                        1.dp,
                                        if (isSelected) Primary else OutlineVariant.copy(alpha = 0.4f),
                                        RoundedCornerShape(9999.dp)
                                    )
                                    .clickable { onFilterChipSelect(filter) }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (filter == "Needs Review") {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFBA1A1A))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    } else if (filter == "100% Deductible" && !isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Primary)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = filter,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Stream section header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANSACTIONS STREAM",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Sorted by Date",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                }
            }

            // List of Expenses
            if (expenses.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainerLowest)
                            .border(1.dp, OutlineVariant.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Ledger Clean & Ready",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No expenses logged yet. Tap '+' below or scan receipts to log write-offs from the beginning.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Secondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddExpenseClick,
                            shape = RoundedCornerShape(9999.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add First Expense", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(expenses, key = { it.id }) { item ->
                    ExpenseLedgerCard(
                        item = item,
                        currency = currency
                    )
                }
            }
        }

        // Floating Action Button (+)
        FloatingActionButton(
            onClick = onAddExpenseClick,
            containerColor = Primary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 125.dp)
                .testTag("add_expense_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Expense",
                modifier = Modifier.size(28.dp)
            )
        }

        // Bottom Sticky Summary Drawer / Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 60.dp)
                .background(Surface.copy(alpha = 0.95f))
                .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "TOTAL Q4 DEDUCTIBLE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = currency.format(totalQ4Deductible),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "saved",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = onExportClick,
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    modifier = Modifier.testTag("export_csv_pdf_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Export CSV/PDF",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseLedgerCard(
    item: ExpenseItem,
    currency: AppCurrency
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariant.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.weight(1f)
            ) {
                // Icon based on type
                val (icon, bg, tint) = when (item.iconType) {
                    ExpenseIconType.DESIGN -> Triple(Icons.Default.DesignServices, Primary.copy(alpha = 0.1f), Primary)
                    ExpenseIconType.CAFE -> Triple(Icons.Default.LocalCafe, SecondaryContainer.copy(alpha = 0.5f), Secondary)
                    ExpenseIconType.CAR -> Triple(Icons.Default.DirectionsCar, Primary.copy(alpha = 0.1f), Primary)
                    ExpenseIconType.CHAIR -> Triple(Icons.Default.Chair, Primary.copy(alpha = 0.1f), Primary)
                    ExpenseIconType.SERVER -> Triple(Icons.Default.Dns, Primary.copy(alpha = 0.1f), Primary)
                    else -> Triple(Icons.Default.ReceiptLong, Primary.copy(alpha = 0.1f), Primary)
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.vendor,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = currency.format(item.amountInBaseInr),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (item.deductibleBadgeText.contains("Depreciation", true)) "Depreciation Q4" else "Tax saved ${currency.format(item.taxSavedInBaseInr)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Card footer divider & badge row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SurfaceContainer.copy(alpha = 0.6f))
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val badgeBg = if (item.deductiblePercentage == 100) Primary.copy(alpha = 0.1f) else SurfaceContainer
            val badgeColor = if (item.deductiblePercentage == 100) Primary else MaterialTheme.colorScheme.onSurfaceVariant

            Text(
                text = item.deductibleBadgeText,
                style = MaterialTheme.typography.labelSmall,
                color = badgeColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(badgeBg)
                    .border(1.dp, if (item.deductiblePercentage == 100) Primary.copy(alpha = 0.2f) else OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(9999.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                val statusIcon = when {
                    item.statusTagText.contains("AI Verified", true) -> Icons.Default.CheckCircle
                    item.statusTagText.contains("AI Categorized", true) -> Icons.Default.AutoAwesome
                    item.statusTagText.contains("Receipt Attached", true) -> Icons.Default.AttachFile
                    item.statusTagText.contains("Auto-synced", true) -> Icons.Default.AccountBalance
                    else -> Icons.Default.Verified
                }
                val iconTint = if (item.statusTagText.contains("AI Verified", true) || item.statusTagText.contains("Verified", true)) Primary else Secondary

                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.statusTagText,
                    style = MaterialTheme.typography.labelSmall,
                    color = iconTint,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
