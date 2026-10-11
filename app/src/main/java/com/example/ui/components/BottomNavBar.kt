package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.Surface
import com.example.viewmodel.ScreenTab

@Composable
fun TaxFlowBottomNavBar(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, OutlineVariant.copy(alpha = 0.25f), RoundedCornerShape(0.dp))
            .background(Surface)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavBarItem(
            label = "Dashboard",
            selected = currentTab == ScreenTab.DASHBOARD,
            filledIcon = Icons.Filled.Dashboard,
            outlinedIcon = Icons.Outlined.Dashboard,
            testTag = "nav_dashboard",
            onClick = { onTabSelected(ScreenTab.DASHBOARD) }
        )

        NavBarItem(
            label = "Expenses",
            selected = currentTab == ScreenTab.EXPENSES,
            filledIcon = Icons.Filled.ReceiptLong,
            outlinedIcon = Icons.Outlined.ReceiptLong,
            testTag = "nav_expenses",
            onClick = { onTabSelected(ScreenTab.EXPENSES) }
        )

        NavBarItem(
            label = "Mileage",
            selected = currentTab == ScreenTab.MILEAGE,
            filledIcon = Icons.Filled.DirectionsCar,
            outlinedIcon = Icons.Outlined.DirectionsCar,
            testTag = "nav_mileage",
            onClick = { onTabSelected(ScreenTab.MILEAGE) }
        )

        NavBarItem(
            label = "Account",
            selected = currentTab == ScreenTab.ACCOUNT,
            filledIcon = Icons.Filled.Person,
            outlinedIcon = Icons.Outlined.Person,
            testTag = "nav_account",
            onClick = { onTabSelected(ScreenTab.ACCOUNT) }
        )
    }
}

@Composable
private fun NavBarItem(
    label: String,
    selected: Boolean,
    filledIcon: ImageVector,
    outlinedIcon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) Primary.copy(alpha = 0.12f) else Surface,
        label = "navBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) Primary else Secondary,
        label = "navContent"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(9999.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = if (selected) filledIcon else outlinedIcon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}
