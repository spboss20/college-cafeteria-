package com.example

import com.example.data.model.CartItem
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderStatus
import com.example.data.model.OutletId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CampusBiteUnitTest {

    @Test
    fun testOutletDataIntegrity() {
        assertEquals("Tasteholic Café", OutletId.TASTEHOLIC.displayName)
        assertEquals("E-Block Cafeteria", OutletId.EBLOCK.displayName)
        assertTrue(OutletId.TASTEHOLIC.location.contains("RT"))
        assertTrue(OutletId.EBLOCK.location.contains("E Block"))
    }

    @Test
    fun testCartCalculation() {
        val pizza = MenuItemEntity(
            id = "th_1",
            outletId = OutletId.TASTEHOLIC.idStr,
            name = "Margherita Pizza",
            description = "Crispy crust with mozzarella",
            price = 149.0,
            category = "Pizza"
        )
        val coffee = MenuItemEntity(
            id = "th_11",
            outletId = OutletId.TASTEHOLIC.idStr,
            name = "Cold Coffee",
            description = "Rich chilled espresso",
            price = 79.0,
            category = "Beverages"
        )

        val cart = listOf(
            CartItem(menuItem = pizza, quantity = 2),
            CartItem(menuItem = coffee, quantity = 1)
        )

        val total = cart.sumOf { it.menuItem.price * it.quantity }
        assertEquals(377.0, total, 0.01)
    }

    @Test
    fun testOrderStatusProgression() {
        val stepPlaced = OrderStatus.PLACED.stepIndex
        val stepPaid = OrderStatus.PAYMENT_CONFIRMED.stepIndex
        val stepPreparing = OrderStatus.PREPARING.stepIndex
        val stepReady = OrderStatus.READY_FOR_PICKUP.stepIndex
        val stepCollected = OrderStatus.COLLECTED.stepIndex

        assertTrue(stepPaid > stepPlaced)
        assertTrue(stepPreparing > stepPaid)
        assertTrue(stepReady > stepPreparing)
        assertTrue(stepCollected > stepReady)
    }
}
