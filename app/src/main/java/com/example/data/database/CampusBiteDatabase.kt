package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.MenuDao
import com.example.data.dao.OrderDao
import com.example.data.dao.OutletSettingDao
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.OutletId
import com.example.data.model.OutletSettingEntity
import com.example.data.model.PaymentStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MenuItemEntity::class, OrderEntity::class, OutletSettingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CampusBiteDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao
    abstract fun orderDao(): OrderDao
    abstract fun outletSettingDao(): OutletSettingDao

    companion object {
        @Volatile
        private var INSTANCE: CampusBiteDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CampusBiteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusBiteDatabase::class.java,
                    "campusbite_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: CampusBiteDatabase) {
            val menuDao = database.menuDao()
            val outletDao = database.outletSettingDao()
            val orderDao = database.orderDao()

            // Outlet settings
            outletDao.setOutletSetting(
                OutletSettingEntity(
                    outletId = OutletId.TASTEHOLIC.idStr,
                    isOpen = true,
                    estimatedPrepTimeMinutes = 15,
                    announcement = "Fresh pizzas, thick shakes & burgers made to order!"
                )
            )
            outletDao.setOutletSetting(
                OutletSettingEntity(
                    outletId = OutletId.EBLOCK.idStr,
                    isOpen = true,
                    estimatedPrepTimeMinutes = 10,
                    announcement = "Authentic spicy Chinese, hot samosas & cutting chai!"
                )
            )

            // Outlet A: Tasteholic Café Menu Items
            val tasteholicItems = listOf(
                MenuItemEntity(
                    id = "th_1",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Margherita Pizza",
                    description = "Classic hand-tossed crust with rich tomato sauce, Italian herbs & melted mozzarella",
                    price = 149.0,
                    category = "Pizza",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 15,
                    iconEmoji = "🍕",
                    customizationOptions = "Regular Crust|Cheese Burst (+₹30)|Spicy Jalapeno"
                ),
                MenuItemEntity(
                    id = "th_2",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Farmhouse Pizza",
                    description = "Loaded with crisp capsicum, diced onions, sweet corn, mushrooms & double cheese",
                    price = 189.0,
                    category = "Pizza",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 18,
                    iconEmoji = "🍕",
                    customizationOptions = "Thin Crust|Cheese Burst (+₹30)|Extra Olives"
                ),
                MenuItemEntity(
                    id = "th_3",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Veg Burger",
                    description = "Crispy spiced potato & vegetable patty topped with iceberg lettuce, creamy mayo & tomatoes",
                    price = 79.0,
                    category = "Burgers",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 10,
                    iconEmoji = "🍔",
                    customizationOptions = "Standard|Add Cheese Slice (+₹15)|Double Patty (+₹35)"
                ),
                MenuItemEntity(
                    id = "th_4",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Cheese Sandwich",
                    description = "Jumbo toasted sandwich packed with shredded mozzarella, cheddar & secret herb spices",
                    price = 89.0,
                    category = "Sandwiches",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 10,
                    iconEmoji = "🥪",
                    customizationOptions = "White Bread|Brown Bread|Extra Butter"
                ),
                MenuItemEntity(
                    id = "th_5",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Grilled Sandwich",
                    description = "Tri-color grilled sandwich stuffed with seasoned bell peppers, corn, paneer & mint chutney",
                    price = 109.0,
                    category = "Sandwiches",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 12,
                    iconEmoji = "🥪",
                    customizationOptions = "Medium Spice|Extra Cheesy (+₹20)|Mint Dip"
                ),
                MenuItemEntity(
                    id = "th_6",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "White Sauce Pasta",
                    description = "Penne in velvety rich Alfredo cream sauce infused with roasted garlic & sweet corn",
                    price = 139.0,
                    category = "Pasta",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 15,
                    iconEmoji = "🍝",
                    customizationOptions = "Creamy Mild|Add Paneer (+₹25)|Extra Black Olives"
                ),
                MenuItemEntity(
                    id = "th_7",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Red Sauce Pasta",
                    description = "Zesty tangy arrabbiata sauce tossed with penne, fresh basil & chilli flakes",
                    price = 129.0,
                    category = "Pasta",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 14,
                    iconEmoji = "🍝",
                    customizationOptions = "Medium Spicy|Extra Spicy Fire|Add Mozzarella (+₹20)"
                ),
                MenuItemEntity(
                    id = "th_8",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Veg Chowmein",
                    description = "Wok-tossed noodles with crunchy julienned veggies, soya sauce & oriental aromatics",
                    price = 99.0,
                    category = "Fast Food",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 10,
                    iconEmoji = "🍜",
                    customizationOptions = "Mild Spice|Spicy Schezwan|No Garlic"
                ),
                MenuItemEntity(
                    id = "th_9",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "French Fries",
                    description = "Crispy golden salted fries served piping hot with peri peri spice mix & mayo dip",
                    price = 69.0,
                    category = "Sides",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 8,
                    iconEmoji = "🍟",
                    customizationOptions = "Classic Salted|Peri-Peri Shaker (+₹10)|Cheesy Jalapeno Dip"
                ),
                MenuItemEntity(
                    id = "th_10",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Garlic Bread",
                    description = "Toasted baguette slices smothered in garlic herb butter & melted cheddar cheese",
                    price = 89.0,
                    category = "Sides",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 10,
                    iconEmoji = "🥖",
                    customizationOptions = "Plain Garlic|Loaded Cheese (+₹20)|Sweet Corn Topping"
                ),
                MenuItemEntity(
                    id = "th_11",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Cold Coffee",
                    description = "Rich, thick & frothy blended espresso with chilled milk and chocolate drizzle",
                    price = 79.0,
                    category = "Beverages",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 5,
                    iconEmoji = "🧋",
                    customizationOptions = "Standard Sweet|Less Sugar|Add Vanilla Ice Cream Scoop (+₹25)"
                ),
                MenuItemEntity(
                    id = "th_12",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Cappuccino",
                    description = "Steaming hot barista espresso topped with creamy dense foam & cocoa dusting",
                    price = 65.0,
                    category = "Beverages",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 5,
                    iconEmoji = "☕",
                    customizationOptions = "Regular|Strong Double Shot|Sugar Free"
                ),
                MenuItemEntity(
                    id = "th_13",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Chocolate Shake",
                    description = "Indulgent thick shake crafted with Belgian cocoa, chocolate fudge & choco chips",
                    price = 99.0,
                    category = "Beverages",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 6,
                    iconEmoji = "🥤",
                    customizationOptions = "Regular|Extra Thick|Choco-Chips Loaded"
                ),
                MenuItemEntity(
                    id = "th_14",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Oreo Shake",
                    description = "Creamy thick shake blended with crunchy Oreo cookies & sweet vanilla cream",
                    price = 109.0,
                    category = "Beverages",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 6,
                    iconEmoji = "🍪",
                    customizationOptions = "Regular Sweet|Double Crushed Oreo|Whipped Cream"
                ),
                MenuItemEntity(
                    id = "th_15",
                    outletId = OutletId.TASTEHOLIC.idStr,
                    name = "Brownie",
                    description = "Fudgy warm walnut chocolate brownie drizzled with hot dark chocolate sauce",
                    price = 85.0,
                    category = "Desserts",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 5,
                    iconEmoji = "🍫",
                    customizationOptions = "Warm Brownie|Brownie with Ice Cream (+₹30)"
                )
            )

            // Outlet B: E-Block Cafeteria Menu Items
            val eBlockItems = listOf(
                MenuItemEntity(
                    id = "eb_1",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Veg Chowmein",
                    description = "Desi style spicy wok noodles tossed with cabbage, carrots, onion & green chilies",
                    price = 70.0,
                    category = "Fast Food",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 8,
                    iconEmoji = "🍜",
                    customizationOptions = "Desi Spicy|Extra Chilli|Less Oil"
                ),
                MenuItemEntity(
                    id = "eb_2",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Hakka Noodles",
                    description = "Light savory wok-fried noodles with bell peppers, spring onion & garlic soy sauce",
                    price = 80.0,
                    category = "Fast Food",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 10,
                    iconEmoji = "🥢",
                    customizationOptions = "Standard|Add Manchurian Balls (+₹30)|Spicy Red"
                ),
                MenuItemEntity(
                    id = "eb_3",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "White Sauce Pasta",
                    description = "Creamy cheesy macaroni pasta cooked with herbs, sweet corn & butter",
                    price = 110.0,
                    category = "Pasta",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 12,
                    iconEmoji = "🍝",
                    customizationOptions = "Creamy|Extra Pepper|Double Cheese (+₹25)"
                ),
                MenuItemEntity(
                    id = "eb_4",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Red Sauce Pasta",
                    description = "Tangy tomato & herb sauce pasta with roasted cumin, green peppers & chili flakes",
                    price = 99.0,
                    category = "Pasta",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 12,
                    iconEmoji = "🍝",
                    customizationOptions = "Medium Spicy|Spicy Arrabbiata|Add Cheese (+₹20)"
                ),
                MenuItemEntity(
                    id = "eb_5",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Veg Pizza",
                    description = "Fresh baked pan pizza topped with crunchy onions, capsicum, tomato slices & cheese",
                    price = 120.0,
                    category = "Pizza",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 15,
                    iconEmoji = "🍕",
                    customizationOptions = "Regular Crust|Extra Toppings (+₹20)|Cheesy Bite"
                ),
                MenuItemEntity(
                    id = "eb_6",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Cheese Sandwich",
                    description = "Double decker toasted bread loaded with melted processed cheese and chatpata masala",
                    price = 70.0,
                    category = "Sandwiches",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 8,
                    iconEmoji = "🥪",
                    customizationOptions = "Crispy Toasted|Soft Buttered|Add Green Chutney"
                ),
                MenuItemEntity(
                    id = "eb_7",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Veg Burger",
                    description = "Crispy vegetable tikki in fresh sesame bun with tangy burger dressing & onion rings",
                    price = 60.0,
                    category = "Burgers",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 8,
                    iconEmoji = "🍔",
                    customizationOptions = "Standard|Add Cheese Slice (+₹15)|Spicy Chutney"
                ),
                MenuItemEntity(
                    id = "eb_8",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Veg Momos (Steamed)",
                    description = "6 pcs delicate steamed dumplings filled with spiced cabbage, carrots & ginger, with spicy red chutney",
                    price = 60.0,
                    category = "Momos",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 7,
                    iconEmoji = "🥟",
                    customizationOptions = "Steamed 6 Pcs|Steamed 10 Pcs (+₹35)|Extra Fiery Chutney"
                ),
                MenuItemEntity(
                    id = "eb_9",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Fried Momos",
                    description = "6 pcs golden crispy fried momos served with creamy mayo dip and fiery red chili sauce",
                    price = 70.0,
                    category = "Momos",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 8,
                    iconEmoji = "🥟",
                    customizationOptions = "Crispy Fried 6 Pcs|Kurkure Coating (+₹20)|Extra Mayo"
                ),
                MenuItemEntity(
                    id = "eb_10",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Masala Maggi",
                    description = "Campus favourite 2-minute noodles cooked with fresh veggies, extra tastemaker & butter",
                    price = 45.0,
                    category = "Quick Bites",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 6,
                    iconEmoji = "🍲",
                    customizationOptions = "Plain Masala|Double Butter (+₹15)|Cheese Maggi (+₹25)"
                ),
                MenuItemEntity(
                    id = "eb_11",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Paneer Roll",
                    description = "Warm paratha wrap rolled with marinated spicy paneer tikka, crunchy onions & mint mayo",
                    price = 85.0,
                    category = "Rolls",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 10,
                    iconEmoji = "🌯",
                    customizationOptions = "Standard Roll|Extra Paneer (+₹25)|Add Grated Cheese (+₹20)"
                ),
                MenuItemEntity(
                    id = "eb_12",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Samosa",
                    description = "Hot crispy pastry packed with spicy potato and green peas filling, with imli chutney",
                    price = 20.0,
                    category = "Snacks",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 2,
                    iconEmoji = "🥟",
                    customizationOptions = "1 Pc with Chutney|2 Pcs Plate (₹40)|With Chole Gravy (+₹25)"
                ),
                MenuItemEntity(
                    id = "eb_13",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Masala Chai",
                    description = "Brewed authentic campus cutting chai infused with fresh ginger, cardamom & cloves",
                    price = 20.0,
                    category = "Beverages",
                    isVeg = true,
                    isBestseller = true,
                    prepTimeMinutes = 4,
                    iconEmoji = "🫖",
                    customizationOptions = "Regular Sweet|Strong Adrak|Less Sugar"
                ),
                MenuItemEntity(
                    id = "eb_14",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Cold Coffee",
                    description = "Refreshing chilled blended coffee with creamy milk froth and cocoa powder",
                    price = 60.0,
                    category = "Beverages",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 5,
                    iconEmoji = "🧋",
                    customizationOptions = "Sweet Frothy|Light Sugar|Extra Cocoa"
                ),
                MenuItemEntity(
                    id = "eb_15",
                    outletId = OutletId.EBLOCK.idStr,
                    name = "Cold Drinks (300ml)",
                    description = "Chilled carbonated soft drinks (Thums Up, Sprite, Coke, Limca)",
                    price = 40.0,
                    category = "Beverages",
                    isVeg = true,
                    isBestseller = false,
                    prepTimeMinutes = 1,
                    iconEmoji = "🥤",
                    customizationOptions = "Thums Up|Sprite|Coca Cola|Limca"
                )
            )

            menuDao.insertAllMenuItems(tasteholicItems)
            menuDao.insertAllMenuItems(eBlockItems)

            // Seed sample orders for realistic dashboard demonstration
            val sampleOrder1 = OrderEntity(
                orderId = "CB-1042",
                studentName = "Aman Sharma",
                studentEmail = "aman.cs24@jecrc.ac.in",
                studentPhone = "+91 98290 12345",
                studentBlock = "CS Block / Library",
                outletId = OutletId.TASTEHOLIC.idStr,
                outletName = OutletId.TASTEHOLIC.displayName,
                outletLocation = OutletId.TASTEHOLIC.location,
                itemsSummary = "1x Margherita Pizza, 1x Cold Coffee",
                totalAmount = 228.0,
                subtotal = 228.0,
                packagingFee = 0.0,
                discount = 0.0,
                specialInstructions = "Please make the coffee extra chilled",
                pickupSlot = "12:15 PM - 12:30 PM",
                pickupToken = "CB-QR-891042",
                orderStatus = OrderStatus.READY_FOR_PICKUP.name,
                paymentStatus = PaymentStatus.SIMULATED_SUCCESS.name,
                paymentMethod = "UPI (@okaxis)",
                transactionRef = "UPI/DEMO/89104212",
                createdAtEpoch = System.currentTimeMillis() - 20 * 60 * 1000,
                estimatedPickupTimeEpoch = System.currentTimeMillis() - 5 * 60 * 1000
            )

            val sampleOrder2 = OrderEntity(
                orderId = "CB-1043",
                studentName = "Priya Verma",
                studentEmail = "priya.ec24@jecrc.ac.in",
                studentPhone = "+91 98765 43210",
                studentBlock = "E Block Ground Floor",
                outletId = OutletId.EBLOCK.idStr,
                outletName = OutletId.EBLOCK.displayName,
                outletLocation = OutletId.EBLOCK.location,
                itemsSummary = "2x Samosa, 1x Masala Chai, 1x Veg Momos",
                totalAmount = 120.0,
                subtotal = 120.0,
                packagingFee = 0.0,
                discount = 0.0,
                specialInstructions = "Extra spicy chutney for momos",
                pickupSlot = "ASAP (10-15 mins)",
                pickupToken = "CB-QR-891043",
                orderStatus = OrderStatus.PREPARING.name,
                paymentStatus = PaymentStatus.SIMULATED_SUCCESS.name,
                paymentMethod = "UPI (@paytm)",
                transactionRef = "UPI/DEMO/89104354",
                createdAtEpoch = System.currentTimeMillis() - 8 * 60 * 1000,
                estimatedPickupTimeEpoch = System.currentTimeMillis() + 7 * 60 * 1000
            )

            orderDao.insertOrder(sampleOrder1)
            orderDao.insertOrder(sampleOrder2)
        }
    }
}
