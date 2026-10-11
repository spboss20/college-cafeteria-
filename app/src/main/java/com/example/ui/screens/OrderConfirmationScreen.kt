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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QrCodeView
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.viewmodel.CampusBiteViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderConfirmationScreen(
    viewModel: CampusBiteViewModel
) {
    BackHandler {
        viewModel.navigateTo(Screen.OUTLET_SELECT)
    }

    val activeOrderId by viewModel.activeOrderId.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val order = allOrders.find { it.orderId == activeOrderId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Confirmed!", fontWeight = FontWeight.Bold, color = CharcoalDark) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.OUTLET_SELECT) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Home", tint = CharcoalDark)
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
                Text("Order details loading...", color = Color(0xFF64748B))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Badge
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = CompletedGreen,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Order Placed Successfully!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = CharcoalDark
                )

                Text(
                    text = "Order ID: ${order.orderId}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CrimsonPrimary
                )

                Text(
                    text = "Pickup Location: ${order.outletName} (${order.outletLocation})",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // The Pickup QR Code Card
                QrCodeView(
                    token = order.pickupToken,
                    sizeDp = 170,
                    modifier = Modifier.testTag("order_confirmation_qr")
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Items Ordered", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(order.itemsSummary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = CharcoalDark)

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Paid", fontSize = 13.sp, color = Color(0xFF64748B))
                            Text("₹${order.totalAmount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CharcoalDark)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment Status", fontSize = 13.sp, color = Color(0xFF64748B))
                            Text(order.paymentStatus, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CompletedGreen)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Selected Slot", fontSize = 13.sp, color = Color(0xFF64748B))
                            Text(order.pickupSlot, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = AmberSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Button(
                    onClick = {
                        viewModel.setActiveOrder(order.orderId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("track_order_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Timeline, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Live Order Tracking", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.navigateTo(Screen.OUTLET_SELECT)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Return to Outlets")
                }
            }
        }
    }
}
