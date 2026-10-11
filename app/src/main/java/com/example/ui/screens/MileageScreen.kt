package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCurrency
import com.example.model.DriveClassification
import com.example.model.MileageDrive
import com.example.ui.theme.Outline
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.Secondary
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MileageScreen(
    currency: AppCurrency,
    isAutoDriveActive: Boolean,
    unclassifiedDrives: List<MileageDrive>,
    monthlyLoggedKm: Double,
    monthlyWriteOffInr: Double,
    onToggleAutoDrive: () -> Unit,
    onClassifyDrive: (driveId: String, classification: DriveClassification) -> Unit,
    onUndoLastClassification: () -> Unit,
    onSimulateTripClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val dragOffsetX = remember { Animatable(0f) }
    val dragOffsetY = remember { Animatable(0f) }

    val infinitePulse = rememberInfiniteTransition(label = "pulse")
    val dotAlpha by infinitePulse.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    val topDrive = unclassifiedDrives.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Status & Counter Banner Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live GPS Auto-Detection Status Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(Primary.copy(alpha = 0.1f))
                    .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(9999.dp))
                    .clickable { onToggleAutoDrive() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isAutoDriveActive) Primary.copy(alpha = dotAlpha) else Outline)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAutoDriveActive) "Auto-Drive Active" else "Auto-Drive Paused",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isAutoDriveActive) Primary else Outline,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Counter Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(SurfaceContainer)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Route,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${unclassifiedDrives.size} unclassified drives",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Swipe Visual Cues
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(9999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Secondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Personal (Left)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(Primary.copy(alpha = 0.1f))
                    .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(9999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Business (Right)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Tinder-Style Stacked Card Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(410.dp),
            contentAlignment = Alignment.Center
        ) {
            if (topDrive != null) {
                // Background Depth Card 2
                if (unclassifiedDrives.size > 2) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(370.dp)
                            .offset(y = 16.dp)
                            .scale(0.92f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceContainer)
                            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    )
                }

                // Background Depth Card 1
                if (unclassifiedDrives.size > 1) {
                    val nextDrive = unclassifiedDrives[1]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.96f)
                            .height(385.dp)
                            .offset(y = 8.dp)
                            .scale(0.96f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceContainerLowest)
                            .border(1.dp, OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = nextDrive.dateText,
                                style = MaterialTheme.typography.labelSmall,
                                color = Secondary
                            )
                            Text(
                                text = "${nextDrive.distanceKm} km",
                                style = MaterialTheme.typography.labelSmall,
                                color = Secondary
                            )
                        }
                    }
                }

                // Front Active Interactive Card
                val rotation = (dragOffsetX.value / 35f).coerceIn(-25f, 25f)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset { IntOffset(dragOffsetX.value.roundToInt(), dragOffsetY.value.roundToInt()) }
                        .rotate(rotation)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .pointerInput(topDrive.id) {
                            detectDragGestures(
                                onDragEnd = {
                                    coroutineScope.launch {
                                        if (dragOffsetX.value > 220f) {
                                            // Swipe Right -> Business
                                            dragOffsetX.animateTo(800f, tween(200))
                                            onClassifyDrive(topDrive.id, DriveClassification.BUSINESS)
                                            dragOffsetX.snapTo(0f)
                                            dragOffsetY.snapTo(0f)
                                        } else if (dragOffsetX.value < -220f) {
                                            // Swipe Left -> Personal
                                            dragOffsetX.animateTo(-800f, tween(200))
                                            onClassifyDrive(topDrive.id, DriveClassification.PERSONAL)
                                            dragOffsetX.snapTo(0f)
                                            dragOffsetY.snapTo(0f)
                                        } else {
                                            // Snap back
                                            dragOffsetX.animateTo(0f, spring())
                                            dragOffsetY.animateTo(0f, spring())
                                        }
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    coroutineScope.launch {
                                        dragOffsetX.snapTo(dragOffsetX.value + dragAmount.x)
                                        dragOffsetY.snapTo(dragOffsetY.value + dragAmount.y * 0.4f)
                                    }
                                }
                            )
                        }
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Card Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Commute,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = topDrive.dateText,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = topDrive.timeRangeText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Secondary
                                    )
                                }
                            }
                            Text(
                                text = "Drive #${topDrive.driveNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Secondary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(SurfaceContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        // Stylized GPS Map Visualizer preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceContainerLow)
                                .border(1.dp, OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .padding(10.dp)
                        ) {
                            // Coordinate Dot Grid background & Polyline Canvas
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                // Dot Grid
                                val spacing = 16.dp.toPx()
                                var x = spacing
                                while (x < w) {
                                    var y = spacing
                                    while (y < h) {
                                        drawCircle(
                                            color = Color(0xFF565E74).copy(alpha = 0.2f),
                                            radius = 1.5.dp.toPx(),
                                            center = Offset(x, y)
                                        )
                                        y += spacing
                                    }
                                    x += spacing
                                }

                                // Route curve path
                                val path = Path().apply {
                                    moveTo(w * 0.12f, h * 0.78f)
                                    cubicTo(
                                        w * 0.38f, h * 0.75f,
                                        w * 0.52f, h * 0.48f,
                                        w * 0.88f, h * 0.22f
                                    )
                                }

                                // Outer glow stroke
                                drawPath(
                                    path = path,
                                    color = PrimaryFixed,
                                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                                // Main emerald polyline
                                drawPath(
                                    path = path,
                                    color = Primary,
                                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }

                            // Start pinpoint (bottom-left)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerLowest.copy(alpha = 0.95f))
                                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Secondary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "START",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Secondary
                                        )
                                        Text(
                                            text = topDrive.startLocation,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            // Destination pinpoint (top-right)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerLowest.copy(alpha = 0.95f))
                                    .border(1.dp, Primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Primary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "DESTINATION",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Primary
                                        )
                                        Text(
                                            text = topDrive.destinationLocation,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        // Drive Key Metric Row (Tabular Figures, 3 columns)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = SurfaceContainer,
                                    shape = RoundedCornerShape(0.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Distance",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Secondary
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${topDrive.distanceKm}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "km",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Secondary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(SurfaceContainer)
                            )

                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(
                                    text = "Duration",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Secondary
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${topDrive.durationMins}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "mins",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Secondary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(SurfaceContainer)
                            )

                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(
                                    text = "Tax Write-off",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = currency.format(topDrive.taxWriteOffInBaseInr),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                        }

                        // AI Intelligent Classification Assist Box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Primary.copy(alpha = 0.1f))
                                .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = topDrive.aiSuggestion,
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    // Swipe direction overlay badge indicators
                    if (dragOffsetX.value > 50f) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .rotate(12f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Primary)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "BUSINESS",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    } else if (dragOffsetX.value < -50f) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .rotate(-12f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Secondary)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "PERSONAL",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            } else {
                // Empty state when all drives classified
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "All Drives Classified!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Zero backlog. Auto-Drive will track your next trip.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Secondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onSimulateTripClick,
                        shape = RoundedCornerShape(9999.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simulate New Drive Event")
                    }
                }
            }
        }

        // Swipe Navigation Gesture Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Personal Button (Left)
            OutlinedButton(
                onClick = {
                    if (topDrive != null) {
                        coroutineScope.launch {
                            dragOffsetX.animateTo(-600f, tween(180))
                            onClassifyDrive(topDrive.id, DriveClassification.PERSONAL)
                            dragOffsetX.snapTo(0f)
                            dragOffsetY.snapTo(0f)
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("classify_personal_button"),
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Secondary,
                    containerColor = SurfaceContainerLowest
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Personal",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Undo / Rewind Button
            IconButton(
                onClick = onUndoLastClassification,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), CircleShape)
                    .testTag("undo_drive_button")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Undo Last Classification",
                    tint = Secondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Write-off Button (Right)
            Button(
                onClick = {
                    if (topDrive != null) {
                        coroutineScope.launch {
                            dragOffsetX.animateTo(600f, tween(180))
                            onClassifyDrive(topDrive.id, DriveClassification.BUSINESS)
                            dragOffsetX.snapTo(0f)
                            dragOffsetY.snapTo(0f)
                        }
                    }
                },
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp)
                    .testTag("classify_writeoff_button"),
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Write-off",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Monthly Cumulative Ledger Summary Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceContainerLowest)
                .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "This month's tally",
                        style = MaterialTheme.typography.labelSmall,
                        color = Secondary
                    )
                    Text(
                        text = "${monthlyLoggedKm.roundToInt()} km logged",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Secured Deductions",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${currency.format(monthlyWriteOffInr)} write-off",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}
