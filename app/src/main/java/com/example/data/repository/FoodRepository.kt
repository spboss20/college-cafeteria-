package com.example.data.repository

import com.example.data.dao.MenuDao
import com.example.data.dao.OrderDao
import com.example.data.dao.OutletSettingDao
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OutletSettingEntity
import kotlinx.coroutines.flow.Flow

class FoodRepository(
    private val menuDao: MenuDao,
    private val orderDao: OrderDao,
    private val outletSettingDao: OutletSettingDao
) {
    fun getMenuItems(outletId: String): Flow<List<MenuItemEntity>> =
        menuDao.getMenuItemsByOutlet(outletId)

    fun getAllMenuItems(): Flow<List<MenuItemEntity>> =
        menuDao.getAllMenuItems()

    suspend fun getMenuItemById(id: String): MenuItemEntity? =
        menuDao.getMenuItemById(id)

    suspend fun updateMenuItem(item: MenuItemEntity) =
        menuDao.insertOrUpdateMenuItem(item)

    suspend fun setItemAvailability(id: String, isAvailable: Boolean) =
        menuDao.setItemAvailability(id, isAvailable)

    suspend fun deleteMenuItem(id: String) =
        menuDao.deleteMenuItem(id)

    // Orders
    fun getAllOrders(): Flow<List<OrderEntity>> =
        orderDao.getAllOrders()

    fun getOrdersByOutlet(outletId: String): Flow<List<OrderEntity>> =
        orderDao.getOrdersByOutlet(outletId)

    fun getOrdersByStudent(studentEmail: String): Flow<List<OrderEntity>> =
        orderDao.getOrdersByStudent(studentEmail)

    suspend fun getOrderById(orderId: String): OrderEntity? =
        orderDao.getOrderById(orderId)

    suspend fun getOrderByPickupToken(token: String): OrderEntity? =
        orderDao.getOrderByPickupToken(token)

    suspend fun placeOrder(order: OrderEntity) =
        orderDao.insertOrder(order)

    suspend fun updateOrderStatus(orderId: String, status: String) =
        orderDao.updateOrderStatus(orderId, status)

    suspend fun markOrderCollected(orderId: String, status: String, collectedAt: Long) =
        orderDao.markOrderCollected(orderId, status, collectedAt)

    // Outlet Settings
    fun getOutletSetting(outletId: String): Flow<OutletSettingEntity?> =
        outletSettingDao.getOutletSetting(outletId)

    fun getAllOutletSettings(): Flow<List<OutletSettingEntity>> =
        outletSettingDao.getAllOutletSettings()

    suspend fun updateOutletSetting(setting: OutletSettingEntity) =
        outletSettingDao.setOutletSetting(setting)
}
