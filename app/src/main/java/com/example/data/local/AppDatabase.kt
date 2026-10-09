package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Deal
import com.example.data.model.DealExpense
import com.example.data.model.DealPartner
import com.example.data.model.DealSale
import com.example.data.model.DealStock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Deal::class,
        DealPartner::class,
        DealExpense::class,
        DealStock::class,
        DealSale::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dealDao(): DealDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wood_deal_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateSampleData(database.dealDao())
                    }
                }
            }

            private suspend fun populateSampleData(dao: DealDao) {
                // Populate sample deal: "Old Building, Madanapalle" to help the user immediately understand
                val deal1Id = dao.insertDeal(
                    Deal(
                        name = "Old Building Demolition",
                        location = "Madanapalle",
                        date = "20 Aug 2026",
                        purchasePrice = 100000.0,
                        isCompleted = false,
                        notes = "Old heritage house wood and teak pillars deal"
                    )
                )

                // Partners for Deal 1
                dao.insertPartners(
                    listOf(
                        DealPartner(dealId = deal1Id, name = "Ahmed", amountInvested = 60000.0, phone = "9876543210"),
                        DealPartner(dealId = deal1Id, name = "Rahim", amountInvested = 40000.0, phone = "9876543211")
                    )
                )

                // Expenses for Deal 1 (Transport, Labour, Loading)
                dao.insertExpenses(
                    listOf(
                        DealExpense(dealId = deal1Id, name = "Transport (Truck)", categoryIcon = "🚚", amount = 8000.0, date = "20 Aug 2026"),
                        DealExpense(dealId = deal1Id, name = "Labour (Demolition & Shifting)", categoryIcon = "👷", amount = 9000.0, date = "21 Aug 2026"),
                        DealExpense(dealId = deal1Id, name = "Cutting & Loading", categoryIcon = "🪚", amount = 3000.0, date = "21 Aug 2026")
                    )
                )

                // Stock for Deal 1
                dao.insertStocks(
                    listOf(
                        DealStock(dealId = deal1Id, category = "Pillar 🪵", itemName = "Heavy Teak Pillars", quantity = "8 pieces"),
                        DealStock(dealId = deal1Id, category = "Beam 🪵", itemName = "Ceiling Roof Beams", quantity = "12 pieces"),
                        DealStock(dealId = deal1Id, category = "Door 🚪", itemName = "Antique Main Doors", quantity = "4 sets"),
                        DealStock(dealId = deal1Id, category = "Window 🪟", itemName = "Teak Wood Windows", quantity = "8 sets")
                    )
                )

                // Sales for Deal 1
                dao.insertSale(
                    DealSale(
                        dealId = deal1Id,
                        itemName = "4 Teak Pillars & 2 Doors",
                        quantity = "6 items",
                        customerName = "Suresh Builder",
                        customerPhone = "9123456780",
                        sellingPrice = 90000.0,
                        amountReceived = 75000.0,
                        amountPending = 15000.0,
                        date = "23 Aug 2026"
                    )
                )
                dao.insertSale(
                    DealSale(
                        dealId = deal1Id,
                        itemName = "Ceiling Beams Lot",
                        quantity = "10 beams",
                        customerName = "Ali Furniture",
                        customerPhone = "9988776655",
                        sellingPrice = 60000.0,
                        amountReceived = 60000.0,
                        amountPending = 0.0,
                        date = "24 Aug 2026"
                    )
                )
            }
        }
    }
}
