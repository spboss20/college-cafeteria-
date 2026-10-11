package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.CrimsonPrimary

@Composable
fun OrderTimeline(
    currentStatusStr: String,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        Triple(OrderStatus.PLACED.name, "Order Placed", Icons.Default.Check),
        Triple(OrderStatus.PAYMENT_CONFIRMED.name, "Payment Verified", Icons.Default.Payment),
        Triple(OrderStatus.ACCEPTED.name, "Vendor Accepted", Icons.Default.ThumbUp),
        Triple(OrderStatus.PREPARING.name, "Kitchen Preparing", Icons.Default.Fastfood),
        Triple(OrderStatus.READY_FOR_PICKUP.name, "Ready for Counter Pickup", Icons.Default.QrCode),
        Triple(OrderStatus.COLLECTED.name, "Food Collected", Icons.Default.Check)
    )

    val isCancelled = currentStatusStr == OrderStatus.CANCELLED.name

    if (isCancelled) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFFFEE2E2), CircleShape)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Close, contentDescription = "Cancelled", tint = Color(0xFFB91C1C))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Order Cancelled or Rejected by Outlet", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        return
    }

    val currentStatus = try {
        OrderStatus.valueOf(currentStatusStr)
    } catch (e: Exception) {
        OrderStatus.PLACED
    }

    val currentStep = currentStatus.stepIndex

    Column(modifier = modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, (statusKey, label, icon) ->
            val isCompleted = currentStep >= index
            val isCurrent = currentStep == index

            val circleColor = when {
                isCurrent -> CrimsonPrimary
                isCompleted -> CompletedGreen
                else -> Color(0xFFCBD5E1)
            }

            val textColor = when {
                isCurrent -> CrimsonPrimary
                isCompleted -> Color(0xFF0F172A)
                else -> Color(0xFF94A3B8)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(circleColor, CircleShape)
                        .then(
                            if (isCurrent) Modifier.border(2.dp, Color(0xFFFEE2E2), CircleShape)
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = label,
                        fontSize = if (isCurrent) 14.sp else 12.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else if (isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                        color = textColor
                    )
                    if (isCurrent) {
                        Text(
                            text = "In Progress",
                            fontSize = 10.sp,
                            color = CrimsonPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .padding(start = 13.dp)
                        .width(2.dp)
                        .height(14.dp)
                        .background(if (currentStep > index) CompletedGreen else Color(0xFFE2E8F0))
                )
            }
        }
    }
}
