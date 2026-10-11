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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.data.model.OutletId
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
fun AdminDashboardScreen(
    viewModel: CampusBiteViewModel
) {
    BackHandler {
        viewModel.navigateTo(Screen.WELCOME)
    }

    val allOrders by viewModel.allOrders.collectAsState()
    val allOutletSettings by viewModel.allOutletSettings.collectAsState()

    var selectedOutletFilter by remember { mutableStateOf("All") }

    val filteredOrders = allOrders.filter {
        when (selectedOutletFilter) {
            "Tasteholic" -> it.outletId == OutletId.TASTEHOLIC.idStr
            "E-Block" -> it.outletId == OutletId.EBLOCK.idStr
            else -> true
        }
    }

    val verifiedOrders = filteredOrders.filter { it.orderStatus == OrderStatus.COLLECTED.name || it.orderStatus == OrderStatus.READY_FOR_PICKUP.name }
    val verifiedRevenue = verifiedOrders.sumOf { it.totalAmount }
    val cancelledOrders = filteredOrders.filter { it.orderStatus == OrderStatus.CANCELLED.name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("JECRC Admin Proctor Hub", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = CharcoalDark)
                        Text("Foundation Canteen Oversight", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.WELCOME) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Home", tint = CharcoalDark)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetDemoData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Demo Data", tint = CrimsonPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header Stats Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Assessment, contentDescription = null, tint = CrimsonPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CAMPUS FOOD ANALYTICS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary, letterSpacing = 1.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatBox("Total Orders", "${filteredOrders.size}", CharcoalDark)
                            StatBox("Verified Sales", "₹${verifiedRevenue.toInt()}", CompletedGreen)
                            StatBox("Cancelled", "${cancelledOrders.size}", Color(0xFFB91C1C))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Outlet Filters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filter:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    listOf("All", "Tasteholic", "E-Block").forEach { filter ->
                        val isSelected = selectedOutletFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedOutletFilter = filter },
                            label = { Text(filter, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CrimsonPrimary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Export CSV / Report Simulation
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Export Audit & Sales Report", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CharcoalDark)
                            Text("Download daily audit log for finance office", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Button(
                            onClick = {
                                viewModel.showToast("Exported ${filteredOrders.size} orders to JECRC_CampusBite_Report.csv")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("export_csv_button")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CSV", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("ALL SYSTEM ORDERS (${filteredOrders.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Orders list
            items(filteredOrders) { order ->
                val formattedTime = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(order.createdAtEpoch))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${order.orderId} • ${order.outletName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CrimsonPrimary)
                            Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = CharcoalDark)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Student: ${order.studentName} (${order.studentEmail})", fontSize = 12.sp, color = CharcoalDark)
                        Text("Items: ${order.itemsSummary}", fontSize = 12.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(formattedTime, fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text(order.orderStatus.replace("_", " "), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (order.orderStatus == OrderStatus.COLLECTED.name) CompletedGreen else AmberSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
        Text(label, fontSize = 11.sp, color = Color(0xFF64748B))
    }
}
