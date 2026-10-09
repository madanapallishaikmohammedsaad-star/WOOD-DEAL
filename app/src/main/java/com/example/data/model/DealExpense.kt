package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "deal_expenses",
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
data class DealExpense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dealId: Long,
    val name: String,            // Transport, Labour, Loading, Cutting, Other
    val categoryIcon: String = "📦", // 🚚, 👷, 🏗️, 🪚, 📦
    val amount: Double,          // e.g. 5000.0
    val photoUri: String? = null,// Bill/Slip photo URI
    val date: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
