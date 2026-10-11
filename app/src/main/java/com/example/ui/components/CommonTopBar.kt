package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Error
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryFixedDim
import com.example.ui.theme.Secondary
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLowest

@Composable
fun SystemStatusBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "9:41",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.02).sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SignalCellularAlt,
                contentDescription = "Signal",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "Wi-Fi",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Default.BatteryFull,
                contentDescription = "Battery",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DashboardHeader(
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onWalletClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Surface)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading Profile Avatar with Status
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onProfileClick() }
        ) {
            Box(
                modifier = Modifier.size(42.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Profile Avatar Initials / Stylized Circle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.15f))
                        .border(1.dp, OutlineVariant.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👨‍💻",
                        fontSize = 20.sp
                    )
                }
                // Online pulse dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(PrimaryFixedDim)
                        .border(2.dp, SurfaceContainerLowest, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Good morning, Alex 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = pulseAlpha))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "FY 2024-25 Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Action Buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("notifications_button")
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Secondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Error)
                            .border(1.5.dp, Surface, CircleShape)
                    )
                }
            }
            IconButton(
                onClick = onWalletClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("wallet_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "Wallet",
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun AppHeader(
    title: String,
    subtitle: String? = null,
    onNotificationsClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Surface)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "Logo",
                    tint = Primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Primary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Secondary
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNotificationsClick) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Secondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Primary)
                            .border(1.dp, Surface, CircleShape)
                    )
                }
            }
            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Secondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
