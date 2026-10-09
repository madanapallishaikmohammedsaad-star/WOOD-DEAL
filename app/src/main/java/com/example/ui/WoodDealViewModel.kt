package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Deal
import com.example.data.model.DealExpense
import com.example.data.model.DealPartner
import com.example.data.model.DealSale
import com.example.data.model.DealStock
import com.example.data.model.DealWithDetails
import com.example.data.repository.DealRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BusinessFinancialSummary(
    val totalInvested: Double,
    val totalDealsCost: Double,
    val totalSales: Double,
    val totalCashReceived: Double,
    val totalCustomerPending: Double,
    val totalProfit: Double,
    val activeDealsCount: Int,
    val completedDealsCount: Int
)

class WoodDealViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DealRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = DealRepository(database.dealDao())
    }

    val allDeals: StateFlow<List<DealWithDetails>> = repository.allDeals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeDeals: StateFlow<List<DealWithDetails>> = allDeals.map { list ->
        list.filter { !it.deal.isCompleted }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val completedDeals: StateFlow<List<DealWithDetails>> = allDeals.map { list ->
        list.filter { it.deal.isCompleted }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val financialSummary: StateFlow<BusinessFinancialSummary> = allDeals.map { list ->
        var totalInvested = 0.0
        var totalCost = 0.0
        var totalSales = 0.0
        var totalReceived = 0.0
        var totalPending = 0.0
        var activeCount = 0
        var completedCount = 0

        list.forEach { item ->
            totalInvested += item.totalInvested
            totalCost += item.totalCost
            totalSales += item.totalSales
            totalReceived += item.totalReceived
            totalPending += item.totalPending
            if (item.deal.isCompleted) completedCount++ else activeCount++
        }

        BusinessFinancialSummary(
            totalInvested = totalInvested,
            totalDealsCost = totalCost,
            totalSales = totalSales,
            totalCashReceived = totalReceived,
            totalCustomerPending = totalPending,
            totalProfit = totalSales - totalCost,
            activeDealsCount = activeCount,
            completedDealsCount = completedCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BusinessFinancialSummary(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0)
    )

    private val _selectedDealId = MutableStateFlow<Long?>(null)
    val selectedDealId: StateFlow<Long?> = _selectedDealId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedDeal: StateFlow<DealWithDetails?> = _selectedDealId.flatMapLatest { id ->
        if (id != null) {
            repository.getDealById(id)
        } else {
            flowOf(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun selectDeal(dealId: Long?) {
        _selectedDealId.value = dealId
    }

    fun createDeal(
        deal: Deal,
        partners: List<DealPartner>,
        expenses: List<DealExpense>,
        stocks: List<DealStock>,
        onCreated: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val id = repository.createDealWithInitialData(deal, partners, expenses, stocks)
            _selectedDealId.value = id
            onCreated(id)
        }
    }

    fun toggleDealCompleted(dealId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.setDealCompleted(dealId, isCompleted)
        }
    }

    fun deleteDeal(deal: Deal) {
        viewModelScope.launch {
            if (_selectedDealId.value == deal.id) {
                _selectedDealId.value = null
            }
            repository.deleteDeal(deal)
        }
    }

    fun addPartner(dealId: Long, name: String, amount: Double, phone: String = "") {
        viewModelScope.launch {
            repository.addPartner(
                DealPartner(
                    dealId = dealId,
                    name = name,
                    amountInvested = amount,
                    phone = phone
                )
            )
        }
    }

    fun deletePartner(partner: DealPartner) {
        viewModelScope.launch {
            repository.deletePartner(partner)
        }
    }

    fun addExpense(
        dealId: Long,
        name: String,
        categoryIcon: String,
        amount: Double,
        photoUri: String?,
        date: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.addExpense(
                DealExpense(
                    dealId = dealId,
                    name = name,
                    categoryIcon = categoryIcon,
                    amount = amount,
                    photoUri = photoUri,
                    date = date,
                    notes = notes
                )
            )
        }
    }

    fun deleteExpense(expense: DealExpense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun addStock(
        dealId: Long,
        category: String,
        itemName: String,
        quantity: String,
        photoUri: String?,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.addStock(
                DealStock(
                    dealId = dealId,
                    category = category,
                    itemName = itemName,
                    quantity = quantity,
                    photoUri = photoUri,
                    notes = notes
                )
            )
        }
    }

    fun toggleStockSold(stock: DealStock) {
        viewModelScope.launch {
            repository.updateStock(stock.copy(isSold = !stock.isSold))
        }
    }

    fun deleteStock(stock: DealStock) {
        viewModelScope.launch {
            repository.deleteStock(stock)
        }
    }

    fun addSale(
        dealId: Long,
        itemName: String,
        quantity: String,
        customerName: String,
        customerPhone: String,
        sellingPrice: Double,
        amountReceived: Double,
        photoUri: String?,
        date: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val pending = (sellingPrice - amountReceived).coerceAtLeast(0.0)
            repository.addSale(
                DealSale(
                    dealId = dealId,
                    itemName = itemName,
                    quantity = quantity,
                    customerName = customerName,
                    customerPhone = customerPhone,
                    sellingPrice = sellingPrice,
                    amountReceived = amountReceived,
                    amountPending = pending,
                    photoUri = photoUri,
                    date = date,
                    notes = notes
                )
            )
        }
    }

    fun updateSaleReceived(sale: DealSale, additionalReceived: Double) {
        viewModelScope.launch {
            val newReceived = (sale.amountReceived + additionalReceived).coerceAtMost(sale.sellingPrice)
            val newPending = (sale.sellingPrice - newReceived).coerceAtLeast(0.0)
            repository.updateSale(
                sale.copy(
                    amountReceived = newReceived,
                    amountPending = newPending
                )
            )
        }
    }

    fun deleteSale(sale: DealSale) {
        viewModelScope.launch {
            repository.deleteSale(sale)
        }
    }
}
