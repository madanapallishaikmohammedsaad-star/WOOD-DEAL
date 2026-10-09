package com.example.data.repository

import com.example.data.local.DealDao
import com.example.data.model.Deal
import com.example.data.model.DealExpense
import com.example.data.model.DealPartner
import com.example.data.model.DealSale
import com.example.data.model.DealStock
import com.example.data.model.DealWithDetails
import kotlinx.coroutines.flow.Flow

class DealRepository(private val dealDao: DealDao) {

    val allDeals: Flow<List<DealWithDetails>> = dealDao.getAllDeals()

    fun getDealById(dealId: Long): Flow<DealWithDetails?> = dealDao.getDealById(dealId)

    suspend fun createDealWithInitialData(
        deal: Deal,
        partners: List<DealPartner>,
        expenses: List<DealExpense>,
        stocks: List<DealStock>
    ): Long {
        val dealId = dealDao.insertDeal(deal)
        if (partners.isNotEmpty()) {
            val partnersWithId = partners.map { it.copy(dealId = dealId) }
            dealDao.insertPartners(partnersWithId)
        }
        if (expenses.isNotEmpty()) {
            val expensesWithId = expenses.map { it.copy(dealId = dealId) }
            dealDao.insertExpenses(expensesWithId)
        }
        if (stocks.isNotEmpty()) {
            val stocksWithId = stocks.map { it.copy(dealId = dealId) }
            dealDao.insertStocks(stocksWithId)
        }
        return dealId
    }

    suspend fun updateDeal(deal: Deal) = dealDao.updateDeal(deal)

    suspend fun deleteDeal(deal: Deal) = dealDao.deleteDeal(deal)

    suspend fun setDealCompleted(dealId: Long, isCompleted: Boolean) =
        dealDao.setDealCompleted(dealId, isCompleted)

    suspend fun addPartner(partner: DealPartner): Long = dealDao.insertPartner(partner)

    suspend fun deletePartner(partner: DealPartner) = dealDao.deletePartner(partner)

    suspend fun addExpense(expense: DealExpense): Long = dealDao.insertExpense(expense)

    suspend fun deleteExpense(expense: DealExpense) = dealDao.deleteExpense(expense)

    suspend fun addStock(stock: DealStock): Long = dealDao.insertStock(stock)

    suspend fun updateStock(stock: DealStock) = dealDao.updateStock(stock)

    suspend fun deleteStock(stock: DealStock) = dealDao.deleteStock(stock)

    suspend fun addSale(sale: DealSale): Long = dealDao.insertSale(sale)

    suspend fun updateSale(sale: DealSale) = dealDao.updateSale(sale)

    suspend fun deleteSale(sale: DealSale) = dealDao.deleteSale(sale)
}
