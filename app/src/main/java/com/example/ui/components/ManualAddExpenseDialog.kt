package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppCurrency
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLowest

@Composable
fun ManualAddExpenseDialog(
    currency: AppCurrency,
    onDismiss: () -> Unit,
    onSaveExpense: (vendor: String, amount: Double, category: String, deductiblePercentage: Int, note: String?) -> Unit
) {
    var vendor by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Software & SaaS") }
    var deductiblePercent by remember { mutableIntStateOf(100) }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Software & SaaS", "Office Equipment", "Client Meals", "Travel", "Server Infrastructure", "Supplies")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
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
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Log New Expense",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceContainer)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Secondary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Vendor Input
                OutlinedTextField(
                    value = vendor,
                    onValueChange = { vendor = it },
                    label = { Text("Vendor / Merchant Name") },
                    placeholder = { Text("e.g. Adobe, AWS, Starbucks, Uber") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_vendor_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (${currency.symbol})") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Category Selection
                Text(
                    text = "SELECT CATEGORY",
                    style = MaterialTheme.typography.labelSmall,
                    color = Secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Primary.copy(alpha = 0.12f) else SurfaceContainerLowest)
                                .border(1.dp, if (isSelected) Primary else OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .clickable {
                                    category = cat
                                    if (cat == "Client Meals") {
                                        deductiblePercent = 50
                                    } else {
                                        deductiblePercent = 100
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Primary else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Text(
                                    text = "$deductiblePercent% Deductible",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Deductibility Percentage Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tax Deductibility",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(if (deductiblePercent == 100) Primary else SurfaceContainer)
                                .clickable { deductiblePercent = 100 }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "100%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (deductiblePercent == 100) Color.White else Secondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(if (deductiblePercent == 50) Primary else SurfaceContainer)
                                .clickable { deductiblePercent = 50 }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "50% (Meals)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (deductiblePercent == 50) Color.White else Secondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Note / Business Purpose (Optional)") },
                    placeholder = { Text("e.g. Q4 Client Architecture Sprint meeting") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
                val isValid = vendor.isNotBlank() && parsedAmount > 0.0

                Button(
                    onClick = {
                        if (isValid) {
                            // Convert back to base INR if active currency is not INR
                            val baseInrAmount = parsedAmount / currency.rateFromInr
                            onSaveExpense(vendor.trim(), baseInrAmount, category, deductiblePercent, notes.ifBlank { null })
                        }
                    },
                    enabled = isValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_manual_expense_button"),
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save to Active Ledger",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
