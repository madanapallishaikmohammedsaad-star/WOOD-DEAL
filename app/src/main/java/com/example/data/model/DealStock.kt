package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "deal_stocks",
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
data class DealStock(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dealId: Long,
    val category: String,        // Pillar 🪵, Beam 🪵, Door 🚪, Window 🪟, Plank 🪵, Other 📦
    val itemName: String,        // e.g. "Main Hall Teak Pillars"
    val quantity: String,        // e.g. "12 pieces", "4 tons"
    val photoUri: String? = null,// Wood photo
    val notes: String = "",
    val isSold: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
