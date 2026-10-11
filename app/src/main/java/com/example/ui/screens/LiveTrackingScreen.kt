package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.components.OrderTimeline
import com.example.ui.components.QrCodeView
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.viewmodel.CampusBiteViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTrackingScreen(
    viewModel: CampusBiteViewModel
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val activeOrderId by viewModel.activeOrderId.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val order = allOrders.find { it.orderId == activeOrderId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Order Status", fontWeight = FontWeight.Bold, color = CharcoalDark) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CharcoalDark)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showToast("Order status refreshed live from campus server") }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = CharcoalDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No active order selected.", color = Color(0xFF64748B))
            }
        } else {
            val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(order.createdAtEpoch))
            val isReady = order.orderStatus == OrderStatus.READY_FOR_PICKUP.name
            val isCollected = order.orderStatus == OrderStatus.COLLECTED.name

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ORDER #${order.orderId}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CrimsonPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCollected) Color(0xFFDCFCE7) else if (isReady) Color(0xFFDBEAFE) else Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = order.orderStatus.replace("_", " "),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCollected) Color(0xFF166534) else if (isReady) Color(0xFF1E40AF) else Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = CharcoalDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(order.outletName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CharcoalDark)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(order.outletLocation, fontSize = 12.sp, color = Color(0xFF64748B))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Placed at $formattedTime • Slot: ${order.pickupSlot}", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Readiness Alert Banner
                if (isReady) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, Color(0xFF86EFAC), RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔔", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("YOUR FOOD IS READY FOR PICKUP!", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFF166534))
                                Text("Proceed to ${order.outletName} counter and present your QR code.", fontSize = 11.sp, color = Color(0xFF15803D))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Live Timeline
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("PREPARATION TIMELINE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        OrderTimeline(currentStatusStr = order.orderStatus)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verification QR Token
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("PICKUP VERIFICATION QR", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        QrCodeView(token = order.pickupToken, sizeDp = 160)

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isCollected) "Order collected successfully!" else "Counter staff will scan this to release your order",
                            fontSize = 12.sp,
                            color = if (isCollected) CompletedGreen else Color(0xFF64748B),
                            fontWeight = if (isCollected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Items summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order Content: ${order.itemsSummary}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CharcoalDark)
                        if (order.specialInstructions.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Note: “${order.specialInstructions}”", fontSize = 12.sp, color = Color(0xFF64748B), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                    }
                }
            }
        }
    }
}
