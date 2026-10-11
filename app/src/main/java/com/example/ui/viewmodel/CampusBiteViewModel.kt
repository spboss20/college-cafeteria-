package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.CampusBiteDatabase
import com.example.data.model.CartItem
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.OutletId
import com.example.data.model.OutletSettingEntity
import com.example.data.model.PaymentStatus
import com.example.data.model.UserRole
import com.example.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class Screen {
    WELCOME,
    OUTLET_SELECT,
    MENU,
    ITEM_DETAIL,
    CART,
    CHECKOUT,
    PAYMENT_SIMULATION,
    ORDER_CONFIRMATION,
    LIVE_TRACKING,
    STUDENT_PROFILE,
    VENDOR_LOGIN,
    VENDOR_DASHBOARD,
    VENDOR_QR_SCANNER,
    ADMIN_DASHBOARD,
    OOPS_ACADEMIC
}

data class CurrentUser(
    val name: String = "Saksham Patwa",
    val email: String = "sakshampatwa84@gmail.com",
    val phone: String = "+91 98290 55443",
    val studentBlock: String = "CS Block / Lab 3",
    val role: UserRole = UserRole.STUDENT
)

class CampusBiteViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CampusBiteDatabase.getDatabase(application, viewModelScope)
    val repository = FoodRepository(db.menuDao(), db.orderDao(), db.outletSettingDao())

    // Navigation and screen management
    private val _currentScreen = MutableStateFlow(Screen.WELCOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<Screen>()

    // Current user state
    private val _currentUser = MutableStateFlow(CurrentUser())
    val currentUser: StateFlow<CurrentUser> = _currentUser.asStateFlow()

    // Selected Outlet
    private val _selectedOutlet = MutableStateFlow(OutletId.TASTEHOLIC)
    val selectedOutlet: StateFlow<OutletId> = _selectedOutlet.asStateFlow()

    // Selected Item for detail view
    private val _selectedItemForDetail = MutableStateFlow<MenuItemEntity?>(null)
    val selectedItemForDetail: StateFlow<MenuItemEntity?> = _selectedItemForDetail.asStateFlow()

    // Active order for tracking / confirmation
    private val _activeOrderId = MutableStateFlow<String?>(null)
    val activeOrderId: StateFlow<String?> = _activeOrderId.asStateFlow()

    // Cart State: Outlet-locked
    private val _cartOutletId = MutableStateFlow<String?>(null)
    val cartOutletId: StateFlow<String?> = _cartOutletId.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Checkout Details
    private val _orderInstructions = MutableStateFlow("")
    val orderInstructions: StateFlow<String> = _orderInstructions.asStateFlow()

    private val _pickupSlot = MutableStateFlow("ASAP (15-20 mins)")
    val pickupSlot: StateFlow<String> = _pickupSlot.asStateFlow()

    // Vendor active outlet filter (when role is vendor)
    private val _vendorOutlet = MutableStateFlow(OutletId.TASTEHOLIC)
    val vendorOutlet: StateFlow<OutletId> = _vendorOutlet.asStateFlow()

    // Live streams from DB
    val allOrders: StateFlow<List<OrderEntity>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOutletSettings: StateFlow<List<OutletSettingEntity>> = repository.getAllOutletSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMenuItems: StateFlow<List<MenuItemEntity>> = repository.getAllMenuItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback message toast/snack
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun navigateTo(screen: Screen) {
        screenStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
            return true
        }
        return false
    }

    fun selectOutlet(outlet: OutletId) {
        _selectedOutlet.value = outlet
        navigateTo(Screen.MENU)
    }

    fun openItemDetail(item: MenuItemEntity) {
        _selectedItemForDetail.value = item
        navigateTo(Screen.ITEM_DETAIL)
    }

    fun setOrderInstructions(instructions: String) {
        _orderInstructions.value = instructions
    }

    fun setPickupSlot(slot: String) {
        _pickupSlot.value = slot
    }

    fun showToast(msg: String) {
        _userMessage.value = msg
    }

    fun clearToast() {
        _userMessage.value = null
    }

    // Cart Management: Enforces Single-Outlet Cart Policy
    fun addToCart(item: MenuItemEntity, customization: String = "Standard", qty: Int = 1) {
        val currentOutlet = _cartOutletId.value
        if (currentOutlet != null && currentOutlet != item.outletId && _cartItems.value.isNotEmpty()) {
            // Cannot combine outlets
            showToast("Your cart already contains items from ${if (currentOutlet == OutletId.TASTEHOLIC.idStr) "Tasteholic Café" else "E-Block Cafeteria"}. Please checkout or clear your cart first.")
            return
        }

        _cartOutletId.value = item.outletId
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst {
            it.menuItem.id == item.id && it.selectedCustomization == customization
        }

        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + qty)
        } else {
            currentList.add(CartItem(menuItem = item, quantity = qty, selectedCustomization = customization))
        }

        _cartItems.value = currentList
        showToast("Added ${item.name} to cart")
    }

    fun updateCartQuantity(index: Int, newQty: Int) {
        val currentList = _cartItems.value.toMutableList()
        if (index in currentList.indices) {
            if (newQty <= 0) {
                currentList.removeAt(index)
            } else {
                currentList[index] = currentList[index].copy(quantity = newQty)
            }
            _cartItems.value = currentList
            if (currentList.isEmpty()) {
                _cartOutletId.value = null
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _cartOutletId.value = null
    }

    val cartSubtotal: Double
        get() = _cartItems.value.sumOf { it.menuItem.price * it.quantity }

    val cartTotal: Double
        get() = cartSubtotal // No surprise fees for college students

    fun placeOrderSimulated(paymentMethod: String, isSuccess: Boolean = true): OrderEntity? {
        val items = _cartItems.value
        val outletId = _cartOutletId.value ?: return null
        if (items.isEmpty()) return null

        val outlet = if (outletId == OutletId.TASTEHOLIC.idStr) OutletId.TASTEHOLIC else OutletId.EBLOCK

        val randId = (1000..9999).random()
        val orderId = "CB-$randId"
        val qrToken = "CB-QR-${UUID.randomUUID().toString().take(8).uppercase()}"
        val txRef = "UPI/JECRC/${System.currentTimeMillis().toString().takeLast(8)}"

        val itemsSummary = items.joinToString(", ") { "${it.quantity}x ${it.menuItem.name}" }

        val newOrder = OrderEntity(
            orderId = orderId,
            studentName = _currentUser.value.name,
            studentEmail = _currentUser.value.email,
            studentPhone = _currentUser.value.phone,
            studentBlock = _currentUser.value.studentBlock,
            outletId = outlet.idStr,
            outletName = outlet.displayName,
            outletLocation = outlet.location,
            itemsSummary = itemsSummary,
            totalAmount = cartTotal,
            subtotal = cartSubtotal,
            specialInstructions = _orderInstructions.value,
            pickupSlot = _pickupSlot.value,
            pickupToken = qrToken,
            orderStatus = if (isSuccess) OrderStatus.PAYMENT_CONFIRMED.name else OrderStatus.PLACED.name,
            paymentStatus = if (isSuccess) PaymentStatus.SIMULATED_SUCCESS.name else PaymentStatus.FAILED.name,
            paymentMethod = paymentMethod,
            transactionRef = txRef,
            createdAtEpoch = System.currentTimeMillis(),
            estimatedPickupTimeEpoch = System.currentTimeMillis() + 15 * 60 * 1000
        )

        viewModelScope.launch {
            repository.placeOrder(newOrder)
            clearCart()
            _activeOrderId.value = orderId
            _orderInstructions.value = ""
        }

        return newOrder
    }

    fun switchUserRole(role: UserRole) {
        val updatedUser = when (role) {
            UserRole.STUDENT -> CurrentUser(name = "Saksham Patwa", email = "sakshampatwa84@gmail.com", role = role)
            UserRole.VENDOR_TASTEHOLIC -> {
                _vendorOutlet.value = OutletId.TASTEHOLIC
                CurrentUser(name = "Tasteholic Manager", email = "manager@tasteholic.jecrc.in", role = role)
            }
            UserRole.VENDOR_EBLOCK -> {
                _vendorOutlet.value = OutletId.EBLOCK
                CurrentUser(name = "E-Block Canteen Head", email = "head@eblock.jecrc.in", role = role)
            }
            UserRole.ADMIN -> CurrentUser(name = "Dr. Canteen Proctor", email = "proctor@jecrc.ac.in", role = role)
        }
        _currentUser.value = updatedUser
    }

    fun setVendorOutlet(outlet: OutletId) {
        _vendorOutlet.value = outlet
    }

    // Vendor Operations
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            if (newStatus == OrderStatus.COLLECTED) {
                repository.markOrderCollected(orderId, newStatus.name, System.currentTimeMillis())
            } else {
                repository.updateOrderStatus(orderId, newStatus.name)
            }
            showToast("Order $orderId updated to ${newStatus.label}")
        }
    }

    fun verifyPickupQrToken(tokenInput: String, currentVendorOutletId: String, onResult: (Boolean, String, OrderEntity?) -> Unit) {
        viewModelScope.launch {
            val order = repository.getOrderByPickupToken(tokenInput.trim()) ?: repository.getOrderById(tokenInput.trim())
            if (order == null) {
                onResult(false, "Invalid QR Token or Order ID not found.", null)
                return@launch
            }
            if (order.outletId != currentVendorOutletId) {
                onResult(false, "Outlet mismatch! This order belongs to ${order.outletName}.", order)
                return@launch
            }
            if (order.orderStatus == OrderStatus.COLLECTED.name) {
                val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(order.collectedAtEpoch ?: 0))
                onResult(false, "Duplicate Scan: Order was ALREADY COLLECTED at $formattedTime.", order)
                return@launch
            }
            if (order.orderStatus == OrderStatus.CANCELLED.name) {
                onResult(false, "Cannot pickup: Order is CANCELLED.", order)
                return@launch
            }

            // Valid!
            onResult(true, "Verification SUCCESS! Order eligible for pickup collection.", order)
        }
    }

    fun toggleItemAvailability(item: MenuItemEntity) {
        viewModelScope.launch {
            repository.setItemAvailability(item.id, !item.isAvailable)
            showToast("${item.name} is now ${if (!item.isAvailable) "Available" else "Marked Sold Out"}")
        }
    }

    fun updateItemPrice(item: MenuItemEntity, newPrice: Double) {
        viewModelScope.launch {
            repository.updateMenuItem(item.copy(price = newPrice))
            showToast("Updated ${item.name} price to ₹$newPrice")
        }
    }

    fun addNewMenuItem(item: MenuItemEntity) {
        viewModelScope.launch {
            repository.updateMenuItem(item)
            showToast("Added new menu item ${item.name}")
        }
    }

    fun toggleOutletOpen(outletId: String, currentSetting: OutletSettingEntity?) {
        viewModelScope.launch {
            val updated = currentSetting?.copy(isOpen = !currentSetting.isOpen)
                ?: OutletSettingEntity(outletId = outletId, isOpen = false)
            repository.updateOutletSetting(updated)
            showToast("Outlet is now ${if (updated.isOpen) "Open" else "Closed"}")
        }
    }

    fun updatePrepTimeEstimate(outletId: String, prepMinutes: Int, currentSetting: OutletSettingEntity?) {
        viewModelScope.launch {
            val updated = currentSetting?.copy(estimatedPrepTimeMinutes = prepMinutes)
                ?: OutletSettingEntity(outletId = outletId, estimatedPrepTimeMinutes = prepMinutes)
            repository.updateOutletSetting(updated)
            showToast("Estimated preparation time updated to $prepMinutes mins")
        }
    }

    fun setActiveOrder(orderId: String) {
        _activeOrderId.value = orderId
        navigateTo(Screen.LIVE_TRACKING)
    }

    fun resetDemoData() {
        viewModelScope.launch {
            CampusBiteDatabase.populateInitialData(db)
            showToast("CampusBite demo data successfully refreshed!")
        }
    }
}
