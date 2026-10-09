package com.example

import com.example.data.model.Deal
import com.example.data.model.DealExpense
import com.example.data.model.DealPartner
import com.example.data.model.DealSale
import com.example.data.model.DealStock
import com.example.data.model.DealWithDetails
import com.example.util.CurrencyUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class DealAccountingTest {

  @Test
  fun testDealProfitAndPartnerSplitCalculation() {
    // Deal 1 — Old Building, Madanapalle
    // Purchase price: ₹1,00,000
    // Expenses: ₹20,000
    // Total cost: ₹1,20,000
    // Partners: Ahmed ₹60,000, Rahim ₹40,000 (Ahmed 60%, Rahim 40%)
    // Wood sold: ₹1,50,000
    // Profit = ₹30,000
    // Ahmed profit = ₹18,000, Total return = ₹78,000
    // Rahim profit = ₹12,000, Total return = ₹52,000

    val deal = Deal(
      id = 1L,
      name = "Old Building",
      location = "Madanapalle",
      date = "25 Aug 2026",
      purchasePrice = 100000.0,
      isCompleted = true
    )

    val partners = listOf(
      DealPartner(id = 1L, dealId = 1L, name = "Ahmed", amountInvested = 60000.0),
      DealPartner(id = 2L, dealId = 1L, name = "Rahim", amountInvested = 40000.0)
    )

    val expenses = listOf(
      DealExpense(id = 1L, dealId = 1L, name = "Transport", categoryIcon = "🚚", amount = 12000.0),
      DealExpense(id = 2L, dealId = 1L, name = "Labour", categoryIcon = "👷", amount = 8000.0)
    )

    val sales = listOf(
      DealSale(id = 1L, dealId = 1L, itemName = "Pillars & Doors", sellingPrice = 150000.0, amountReceived = 150000.0, amountPending = 0.0)
    )

    val dealWithDetails = DealWithDetails(
      deal = deal,
      partners = partners,
      expenses = expenses,
      stocks = emptyList(),
      sales = sales
    )

    assertEquals(120000.0, dealWithDetails.totalCost, 0.01)
    assertEquals(150000.0, dealWithDetails.totalSales, 0.01)
    assertEquals(30000.0, dealWithDetails.profit, 0.01)
    assertEquals(100000.0, dealWithDetails.totalInvested, 0.01)

    val payouts = dealWithDetails.partnerPayouts
    assertEquals(2, payouts.size)

    val ahmedPayout = payouts.first { it.name == "Ahmed" }
    assertEquals(60.0, ahmedPayout.percentage, 0.01)
    assertEquals(18000.0, ahmedPayout.profitShare, 0.01)
    assertEquals(78000.0, ahmedPayout.totalReturn, 0.01)

    val rahimPayout = payouts.first { it.name == "Rahim" }
    assertEquals(40.0, rahimPayout.percentage, 0.01)
    assertEquals(12000.0, rahimPayout.profitShare, 0.01)
    assertEquals(52000.0, rahimPayout.totalReturn, 0.01)
  }
}
