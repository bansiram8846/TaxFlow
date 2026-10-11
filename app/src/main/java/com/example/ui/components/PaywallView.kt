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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppCurrency
import com.example.ui.theme.Outline
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.Secondary
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLowest

enum class SubscriptionPlan(
    val title: String,
    val billingPeriod: String,
    val priceInr: Double,
    val badge: String? = null,
    val subtitle: String
) {
    ANNUAL("Annual Pro", "per year", 6499.0, "SAVE 35%", "Billed annually • ₹541/mo"),
    MONTHLY("Monthly Pro", "per month", 799.0, null, "Flexible billing • Cancel anytime"),
    LIFETIME("Lifetime Pass", "one-time", 14999.0, "VIP", "Unlimited forever access")
}

@Composable
fun PaywallDialog(
    reasonMessage: String,
    currency: AppCurrency,
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit,
    onResetTrial: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        PaywallContent(
            reasonMessage = reasonMessage,
            currency = currency,
            onDismiss = onDismiss,
            onUpgrade = onUpgrade,
            onResetTrial = onResetTrial
        )
    }
}

@Composable
fun PaywallContent(
    reasonMessage: String,
    currency: AppCurrency,
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit,
    onResetTrial: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.ANNUAL) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.ui.theme.Surface)
    ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .testTag("paywall_close_button")
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

                // Lock Badge Graphic
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.12f))
                        .border(1.5.dp, Primary.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = Primary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Subscription Required",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = reasonMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Feature Highlights
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FeatureRow(
                        icon = Icons.Default.DocumentScanner,
                        title = "Automated AI Receipt Scanning",
                        desc = "Extract vendor, line items, and deductibility in seconds."
                    )
                    FeatureRow(
                        icon = Icons.Default.DirectionsCar,
                        title = "Unlimited Mileage Logging",
                        desc = "Background GPS auto-detect and swipe classification."
                    )
                    FeatureRow(
                        icon = Icons.Default.Paid,
                        title = "Multi-Currency Live FX",
                        desc = "Instant conversion across USD, EUR, INR, GBP & JPY."
                    )
                    FeatureRow(
                        icon = Icons.Default.IosShare,
                        title = "Schedule C & Tax Filing Exports",
                        desc = "1-click audit-proof tax reports in PDF & CSV format."
                    )
                    FeatureRow(
                        icon = Icons.Default.Security,
                        title = "0.02% Low Audit Risk Shield",
                        desc = "Smart compliance algorithms protecting your write-offs."
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "SELECT YOUR MEMBERSHIP",
                    style = MaterialTheme.typography.labelSmall,
                    color = Secondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Plans
                SubscriptionPlan.values().forEach { plan ->
                    val isSelected = selectedPlan == plan
                    val borderColor = if (isSelected) Primary else OutlineVariant.copy(alpha = 0.4f)
                    val bgColor = if (isSelected) Primary.copy(alpha = 0.05f) else SurfaceContainerLowest

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(bgColor)
                            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable { selectedPlan = plan }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Primary else SurfaceContainer)
                                        .border(1.dp, if (isSelected) Primary else Outline, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = SurfaceContainerLowest,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = plan.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (plan.badge != null) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = plan.badge,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Primary,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Primary.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = plan.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Secondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = currency.format(plan.priceInr),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = plan.billingPeriod,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Secondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CTA Button
                Button(
                    onClick = onUpgrade,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("subscribe_button"),
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = SurfaceContainerLowest
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unlock Pro • ${currency.format(selectedPlan.priceInr)}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Testing Reset button (crucial for reviewers/testers to reset trial!)
                OutlinedButton(
                    onClick = onResetTrial,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("reset_trial_button"),
                    shape = RoundedCornerShape(9999.dp)
                ) {
                    Text(
                        text = "Reset Free 30-Day Trial (Demo Mode)",
                        style = MaterialTheme.typography.labelMedium,
                        color = Secondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "No commitments • 100% Tax-deductible software expense",
                    style = MaterialTheme.typography.bodySmall,
                    color = Secondary,
                    textAlign = TextAlign.Center
                )
            }
        }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = Secondary
            )
        }
    }
}
