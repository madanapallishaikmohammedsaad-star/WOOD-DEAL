package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "deal_sales",
    foreignKeys = [
        ForeignKey(
            entity = Deal::class,
            parentColumns = ["id"],
            childColumns = ["dealId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["dealId"])]
)
data class DealSale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dealId: Long,
    val itemName: String,        // e.g. "5 Pillars and 2 Doors", "Planks Lot"
    val quantity: String = "",   // e.g. "5 pcs"
    val customerName: String = "", // e.g. "Ramesh Contractor"
    val customerPhone: String = "",
    val sellingPrice: Double,    // Total sale amount e.g. 50000.0
    val amountReceived: Double,  // Cash received so far e.g. 40000.0
    val amountPending: Double,   // Remaining pending e.g. 10000.0
    val photoUri: String? = null,// Bill/Slip/Delivery slip photo
    val date: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
