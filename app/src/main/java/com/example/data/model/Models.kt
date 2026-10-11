package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    STUDENT,
    VENDOR_TASTEHOLIC,
    VENDOR_EBLOCK,
    ADMIN
}

enum class OutletId(val idStr: String, val displayName: String, val location: String, val category: String) {
    TASTEHOLIC(
        idStr = "tasteholic",
        displayName = "Tasteholic Café",
        location = "Near RT, JECRC Foundation Campus, Jaipur",
        category = "Café, Fast Food & Beverages"
    ),
    EBLOCK(
        idStr = "eblock",
        displayName = "E-Block Cafeteria",
        location = "E Block, JECRC Foundation Campus, Jaipur",
        category = "Quick Meals, Snacks & Indian Fast Food"
    )
}

enum class OrderStatus(val label: String, val stepIndex: Int) {
    PLACED("Order Placed", 0),
    PAYMENT_CONFIRMED("Payment Confirmed", 1),
    ACCEPTED("Accepted by Vendor", 2),
    PREPARING("Preparing Food", 3),
    READY_FOR_PICKUP("Ready for Pickup", 4),
    COLLECTED("Collected", 5),
    CANCELLED("Cancelled", -1)
}

enum class PaymentStatus(val label: String) {
    SIMULATED_SUCCESS("Paid (Demo Gateway)"),
    UPI_SUCCESS("Paid via UPI"),
    PENDING("Payment Pending"),
    FAILED("Payment Failed")
}

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey val id: String,
    val outletId: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val isVeg: Boolean = true,
    val isAvailable: Boolean = true,
    val isBestseller: Boolean = false,
    val prepTimeMinutes: Int = 12,
    val iconEmoji: String = "🍔",
    val customizationOptions: String = "" // e.g. "Regular|Cheese Loaded|Spicy"
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String, // e.g. "CB-7821"
    val studentName: String,
    val studentEmail: String,
    val studentPhone: String,
    val studentBlock: String,
    val outletId: String,
    val outletName: String,
    val outletLocation: String,
    val itemsSummary: String, // JSON or formatted string
    val totalAmount: Double,
    val subtotal: Double,
    val packagingFee: Double = 0.0,
    val discount: Double = 0.0,
    val specialInstructions: String = "",
    val pickupSlot: String = "ASAP (15-20 mins)",
    val pickupToken: String, // Secure opaque token e.g. "CB-QR-9F8A2D"
    val orderStatus: String, // from OrderStatus
    val paymentStatus: String, // from PaymentStatus
    val paymentMethod: String = "UPI (Simulated)",
    val transactionRef: String,
    val createdAtEpoch: Long = System.currentTimeMillis(),
    val estimatedPickupTimeEpoch: Long = System.currentTimeMillis() + 15 * 60 * 1000,
    val collectedAtEpoch: Long? = null,
    val cancellationReason: String? = null
)

@Entity(tableName = "outlet_settings")
data class OutletSettingEntity(
    @PrimaryKey val outletId: String,
    val isOpen: Boolean = true,
    val estimatedPrepTimeMinutes: Int = 15,
    val announcement: String = "Serving hot & fresh campus meals!",
    val isOrderPaused: Boolean = false
)

data class CartItem(
    val menuItem: MenuItemEntity,
    val quantity: Int,
    val selectedCustomization: String = "Standard"
)
