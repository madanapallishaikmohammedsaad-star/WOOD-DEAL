package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Deal
import com.example.data.model.DealExpense
import com.example.data.model.DealPartner
import com.example.data.model.DealSale
import com.example.data.model.DealStock
import com.example.data.model.DealWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface DealDao {

    @Transaction
    @Query("SELECT * FROM deals ORDER BY createdAt DESC")
    fun getAllDeals(): Flow<List<DealWithDetails>>

    @Transaction
    @Query("SELECT * FROM deals WHERE id = :dealId")
    fun getDealById(dealId: Long): Flow<DealWithDetails?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeal(deal: Deal): Long

    @Update
    suspend fun updateDeal(deal: Deal)

    @Delete
    suspend fun deleteDeal(deal: Deal)

    @Query("UPDATE deals SET isCompleted = :isCompleted WHERE id = :dealId")
    suspend fun setDealCompleted(dealId: Long, isCompleted: Boolean)

    // Partners
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartner(partner: DealPartner): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartners(partners: List<DealPartner>)

    @Delete
    suspend fun deletePartner(partner: DealPartner)

    // Expenses
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: DealExpense): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<DealExpense>)

    @Delete
    suspend fun deleteExpense(expense: DealExpense)

    // Stock
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: DealStock): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStocks(stocks: List<DealStock>)

    @Update
    suspend fun updateStock(stock: DealStock)

    @Delete
    suspend fun deleteStock(stock: DealStock)

    // Sales
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: DealSale): Long

    @Update
    suspend fun updateSale(sale: DealSale)

    @Delete
    suspend fun deleteSale(sale: DealSale)
}
