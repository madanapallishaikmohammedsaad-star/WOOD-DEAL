package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class PartnerPayout(
    val partnerId: Long,
    val name: String,
    val phone: String,
    val amountInvested: Double,
    val percentage: Double,       // e.g. 50.0 (%)
    val profitShare: Double,      // e.g. 15000.0
    val totalReturn: Double       // e.g. 65000.0 (Investment + Profit)
)

data class DealWithDetails(
    @Embedded val deal: Deal,

    @Relation(parentColumn = "id", entityColumn = "dealId")
    val partners: List<DealPartner> = emptyList(),

    @Relation(parentColumn = "id", entityColumn = "dealId")
    val expenses: List<DealExpense> = emptyList(),

    @Relation(parentColumn = "id", entityColumn = "dealId")
    val stocks: List<DealStock> = emptyList(),

    @Relation(parentColumn = "id", entityColumn = "dealId")
    val sales: List<DealSale> = emptyList()
) {
    val totalExpenses: Double
        get() = expenses.sumOf { it.amount }

    val totalCost: Double
        get() = deal.purchasePrice + totalExpenses

    val totalInvested: Double
        get() = partners.sumOf { it.amountInvested }

    val totalSales: Double
        get() = sales.sumOf { it.sellingPrice }

    val totalReceived: Double
        get() = sales.sumOf { it.amountReceived }

    val totalPending: Double
        get() = sales.sumOf { it.amountPending }

    val profit: Double
        get() = totalSales - totalCost

    val isProfitable: Boolean
        get() = profit >= 0

    val partnerPayouts: List<PartnerPayout>
        get() {
            val total = totalInvested
            return partners.map { partner ->
                val pct = if (total > 0) (partner.amountInvested / total) * 100.0 else 0.0
                val share = profit * (pct / 100.0)
                val ret = partner.amountInvested + share
                PartnerPayout(
                    partnerId = partner.id,
                    name = partner.name,
                    phone = partner.phone,
                    amountInvested = partner.amountInvested,
                    percentage = pct,
                    profitShare = share,
                    totalReturn = ret
                )
            }
        }
}
