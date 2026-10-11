package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ReceiptScanResult
import com.example.model.AppCurrency
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLowest

@Composable
fun ReceiptScanDialog(
    isScanning: Boolean,
    scanResult: ReceiptScanResult?,
    currency: AppCurrency,
    onClose: () -> Unit,
    onStartScan: (customText: String?) -> Unit,
    onConfirmExpense: () -> Unit
) {
    var manualInput by remember { mutableStateOf("") }
    var selectedPreset by remember { mutableStateOf<String?>(null) }

    val scanAnimation = rememberInfiniteTransition(label = "scanBeam")
    val beamOffset by scanAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beamOffset"
    )

    Dialog(
        onDismissRequest = onClose,
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
                // Top Bar
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
                                imageVector = Icons.Default.DocumentScanner,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AI Receipt Scanner",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .testTag("scan_close_button")
                            .clip(CircleShape)
                            .background(SurfaceContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated Optical Camera Viewfinder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF0F172A))
                        .border(2.dp, if (isScanning) Primary else OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Viewfinder corner marks
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("┏", color = Primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text("┓", color = Primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("┗", color = Primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text("┛", color = Primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Scanning Laser Beam Animation
                    if (isScanning) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .offset(y = (beamOffset - 90).dp)
                                .background(Primary)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Primary,
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Running Optical OCR & Deductibility Audit...",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    } else if (scanResult == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Align physical receipt or select sample below",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    } else {
                        // Extracted verified badge overlay
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(Primary.copy(alpha = 0.85f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Extracted: ${scanResult.vendor}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sample Receipts Shortcuts
                Text(
                    text = "QUICK TEST SAMPLES (CLICK TO SCAN):",
                    style = MaterialTheme.typography.labelSmall,
                    color = Secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val p1 = "Croma Electronics\nDual 4K Docking Station\nTotal: ₹3,499"
                    val p2 = "Starbucks Coffee\nClient Flat White\nTotal: ₹940"
                    val p3 = "GitHub Inc.\nCopilot AI Sub\nTotal: ₹1,650"

                    PresetPill("💻 Work Dock", isSelected = selectedPreset == p1) {
                        selectedPreset = p1
                        manualInput = p1
                        onStartScan(p1)
                    }
                    PresetPill("☕ Client Cafe", isSelected = selectedPreset == p2) {
                        selectedPreset = p2
                        manualInput = p2
                        onStartScan(p2)
                    }
                    PresetPill("⚡ Software", isSelected = selectedPreset == p3) {
                        selectedPreset = p3
                        manualInput = p3
                        onStartScan(p3)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom OCR input / paste receipt
                OutlinedTextField(
                    value = manualInput,
                    onValueChange = { manualInput = it },
                    label = { Text("Paste receipt text or invoice details") },
                    placeholder = { Text("e.g. Apple Store, MacBook Hub, Total: ₹2,400") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("receipt_custom_text_input"),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Action button: Scan
                Button(
                    onClick = { onStartScan(manualInput.ifBlank { null }) },
                    enabled = !isScanning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("trigger_scan_button"),
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isScanning) "Analyzing with Gemini..." else "Scan & Audit Receipt",
                        fontWeight = FontWeight.Bold
                    )
                }

                // Extracted Result Card
                AnimatedVisibility(visible = scanResult != null) {
                    scanResult?.let { res ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceContainerLowest)
                                .border(1.dp, Primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = res.vendor,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${res.category} • ${res.dateText}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Secondary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = currency.format(res.totalAmount),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Primary
                                    )
                                    Text(
                                        text = "-${currency.format(res.taxSavingsEst)} Tax Saved",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${res.deductiblePercentage}% Deductible",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(Primary.copy(alpha = 0.12f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                                Text(
                                    text = "Confidence: ${(res.confidenceScore * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Secondary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // AI Note
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Primary.copy(alpha = 0.08f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = res.aiNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Primary,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onConfirmExpense,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("confirm_expense_button"),
                                shape = RoundedCornerShape(9999.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add to Expense Ledger",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(if (isSelected) Primary.copy(alpha = 0.15f) else SurfaceContainer)
            .border(1.dp, if (isSelected) Primary else OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(9999.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Primary else Secondary
        )
    }
}
