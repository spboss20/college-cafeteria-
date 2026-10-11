package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.OutletId
import com.example.ui.components.VegIndicator
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
fun VendorDashboardScreen(
    viewModel: CampusBiteViewModel
) {
    BackHandler {
        viewModel.navigateTo(Screen.WELCOME)
    }

    val vendorOutlet by viewModel.vendorOutlet.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val allMenuItems by viewModel.allMenuItems.collectAsState()
    val outletSettings by viewModel.allOutletSettings.collectAsState()

    val currentSetting = outletSettings.find { it.outletId == vendorOutlet.idStr }
    val outletOrders = allOrders.filter { it.outletId == vendorOutlet.idStr }
    val outletMenuItems = allMenuItems.filter { it.outletId == vendorOutlet.idStr }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Orders, 1: Menu Management, 2: Outlet Settings
    var showAddItemDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${vendorOutlet.displayName} Portal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = CharcoalDark
                        )
                        Text(
                            text = "Vendor Management Dashboard",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.WELCOME) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Home", tint = CharcoalDark)
                    }
                },
                actions = {
                    // QR Scanner shortcut
                    Button(
                        onClick = { viewModel.navigateTo(Screen.VENDOR_QR_SCANNER) },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 8.dp).testTag("top_verify_qr_button"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verify QR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Metrics Summary Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val totalOrders = outletOrders.size
                val pendingOrders = outletOrders.count { it.orderStatus == OrderStatus.PLACED.name || it.orderStatus == OrderStatus.PAYMENT_CONFIRMED.name || it.orderStatus == OrderStatus.ACCEPTED.name || it.orderStatus == OrderStatus.PREPARING.name }
                val completedOrders = outletOrders.count { it.orderStatus == OrderStatus.COLLECTED.name }
                val totalSales = outletOrders.filter { it.orderStatus == OrderStatus.COLLECTED.name || it.orderStatus == OrderStatus.READY_FOR_PICKUP.name }.sumOf { it.totalAmount }

                MetricPill(label = "Active Queue", value = "$pendingOrders", color = AmberSecondary)
                MetricPill(label = "Collected", value = "$completedOrders", color = CompletedGreen)
                MetricPill(label = "Verified Sales", value = "₹${totalSales.toInt()}", color = CrimsonPrimary)
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = CrimsonPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Orders (${outletOrders.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Menu (${outletMenuItems.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Outlet Controls", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }

            when (selectedTab) {
                0 -> VendorOrdersList(
                    orders = outletOrders,
                    onUpdateStatus = { orderId, status -> viewModel.updateOrderStatus(orderId, status) },
                    onOpenScanner = { viewModel.navigateTo(Screen.VENDOR_QR_SCANNER) }
                )
                1 -> VendorMenuManagement(
                    menuItems = outletMenuItems,
                    onToggleAvailability = { viewModel.toggleItemAvailability(it) },
                    onAddNewItemClick = { showAddItemDialog = true }
                )
                2 -> VendorOutletControls(
                    outlet = vendorOutlet,
                    setting = currentSetting,
                    onToggleOpen = { viewModel.toggleOutletOpen(vendorOutlet.idStr, currentSetting) },
                    onUpdatePrepTime = { viewModel.updatePrepTimeEstimate(vendorOutlet.idStr, it, currentSetting) }
                )
            }
        }
    }

    if (showAddItemDialog) {
        AddMenuItemDialog(
            outletId = vendorOutlet.idStr,
            onDismiss = { showAddItemDialog = false },
            onAdd = { newItem ->
                viewModel.addNewMenuItem(newItem)
                showAddItemDialog = false
            }
        )
    }
}

@Composable
private fun MetricPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
        Text(label, fontSize = 10.sp, color = Color(0xFF64748B))
    }
}

@Composable
private fun VendorOrdersList(
    orders: List<OrderEntity>,
    onUpdateStatus: (String, OrderStatus) -> Unit,
    onOpenScanner: () -> Unit
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No orders placed yet for this outlet.", color = Color(0xFF94A3B8), fontSize = 14.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(orders) { order ->
                VendorOrderCard(
                    order = order,
                    onUpdateStatus = onUpdateStatus,
                    onOpenScanner = onOpenScanner
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun VendorOrderCard(
    order: OrderEntity,
    onUpdateStatus: (String, OrderStatus) -> Unit,
    onOpenScanner: () -> Unit
) {
    val currentStatus = try {
        OrderStatus.valueOf(order.orderStatus)
    } catch (e: Exception) {
        OrderStatus.PLACED
    }

    val isCollected = currentStatus == OrderStatus.COLLECTED
    val isCancelled = currentStatus == OrderStatus.CANCELLED

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vendor_order_card_${order.orderId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(order.orderId, fontWeight = FontWeight.Black, fontSize = 16.sp, color = CrimsonPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CharcoalDark)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCollected) Color(0xFFDCFCE7) else if (isCancelled) Color(0xFFFEE2E2) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = currentStatus.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCollected) Color(0xFF166534) else if (isCancelled) Color(0xFF991B1B) else Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Student: ${order.studentName} (${order.studentPhone})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CharcoalDark)
            Text("Items: ${order.itemsSummary}", fontSize = 13.sp, color = Color(0xFF334155))
            if (order.specialInstructions.isNotBlank()) {
                Text("Note: “${order.specialInstructions}”", fontSize = 12.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Medium)
            }
            Text("Slot: ${order.pickupSlot} • QR Ref: ${order.pickupToken}", fontSize = 11.sp, color = Color(0xFF94A3B8))

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons by state
            if (!isCollected && !isCancelled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (currentStatus) {
                        OrderStatus.PLACED, OrderStatus.PAYMENT_CONFIRMED -> {
                            Button(
                                onClick = { onUpdateStatus(order.orderId, OrderStatus.ACCEPTED) },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Accept Order", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { onUpdateStatus(order.orderId, OrderStatus.CANCELLED) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Reject", fontSize = 12.sp, color = Color(0xFFB91C1C))
                            }
                        }
                        OrderStatus.ACCEPTED -> {
                            Button(
                                onClick = { onUpdateStatus(order.orderId, OrderStatus.PREPARING) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Start Preparing Food", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        OrderStatus.PREPARING -> {
                            Button(
                                onClick = { onUpdateStatus(order.orderId, OrderStatus.READY_FOR_PICKUP) },
                                colors = ButtonDefaults.buttonColors(containerColor = CompletedGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Mark Ready for Pickup 🔔", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        OrderStatus.READY_FOR_PICKUP -> {
                            Button(
                                onClick = { onOpenScanner() },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scan / Verify Pickup QR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun VendorMenuManagement(
    menuItems: List<MenuItemEntity>,
    onToggleAvailability: (MenuItemEntity) -> Unit,
    onAddNewItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Outlet Menu Items (${menuItems.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CharcoalDark)
            Button(
                onClick = onAddNewItemClick,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(menuItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.iconEmoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CharcoalDark)
                                Text("₹${item.price.toInt()} • ${item.category}", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (item.isAvailable) "In Stock" else "Sold Out",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isAvailable) CompletedGreen else Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = item.isAvailable,
                                onCheckedChange = { onToggleAvailability(item) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = CompletedGreen
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VendorOutletControls(
    outlet: OutletId,
    setting: com.example.data.model.OutletSettingEntity?,
    onToggleOpen: () -> Unit,
    onUpdatePrepTime: (Int) -> Unit
) {
    val isOpen = setting?.isOpen ?: true
    val prepTime = setting?.estimatedPrepTimeMinutes ?: 15

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("STORE OPERATING STATUS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(if (isOpen) "Outlet is OPEN" else "Outlet is CLOSED", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CharcoalDark)
                        Text(if (isOpen) "Accepting online orders from students" else "Pausing new orders temporarily", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                    Switch(
                        checked = isOpen,
                        onCheckedChange = { onToggleOpen() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CompletedGreen)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("ESTIMATED PREPARATION TIME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(10, 15, 20, 30).forEach { mins ->
                        val isSelected = prepTime == mins
                        FilterChip(
                            selected = isSelected,
                            onClick = { onUpdatePrepTime(mins) },
                            label = { Text("$mins mins", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberSecondary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddMenuItemDialog(
    outletId: String,
    onDismiss: () -> Unit,
    onAdd: (MenuItemEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Snacks") }
    var emoji by remember { mutableStateOf("🍲") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Food Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Dish Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Price in INR (₹)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Fast Food, Pasta, Beverages)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 50.0
                    val newItem = MenuItemEntity(
                        id = "${outletId}_${System.currentTimeMillis().toString().takeLast(5)}",
                        outletId = outletId,
                        name = name.ifBlank { "Special Dish" },
                        description = description.ifBlank { "Fresh prepared campus meal" },
                        price = price,
                        category = category.ifBlank { "Specials" },
                        iconEmoji = emoji
                    )
                    onAdd(newItem)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
            ) {
                Text("Add to Menu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
