package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OutletSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    @Query("SELECT * FROM menu_items WHERE outletId = :outletId ORDER BY name ASC")
    fun getMenuItemsByOutlet(outletId: String): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items")
    fun getAllMenuItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE id = :id LIMIT 1")
    suspend fun getMenuItemById(id: String): MenuItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMenuItem(item: MenuItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMenuItems(items: List<MenuItemEntity>)

    @Query("UPDATE menu_items SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun setItemAvailability(id: String, isAvailable: Boolean)

    @Query("DELETE FROM menu_items WHERE id = :id")
    suspend fun deleteMenuItem(id: String)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAtEpoch DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE outletId = :outletId ORDER BY createdAtEpoch DESC")
    fun getOrdersByOutlet(outletId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE studentEmail = :studentEmail ORDER BY createdAtEpoch DESC")
    fun getOrdersByStudent(studentEmail: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE pickupToken = :token LIMIT 1")
    suspend fun getOrderByPickupToken(token: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET orderStatus = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    @Query("UPDATE orders SET orderStatus = :status, collectedAtEpoch = :collectedAt WHERE orderId = :orderId")
    suspend fun markOrderCollected(orderId: String, status: String, collectedAt: Long)
}

@Dao
interface OutletSettingDao {
    @Query("SELECT * FROM outlet_settings WHERE outletId = :outletId LIMIT 1")
    fun getOutletSetting(outletId: String): Flow<OutletSettingEntity?>

    @Query("SELECT * FROM outlet_settings")
    fun getAllOutletSettings(): Flow<List<OutletSettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setOutletSetting(setting: OutletSettingEntity)
}
