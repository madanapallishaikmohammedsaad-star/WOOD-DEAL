package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "deal_partners",
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
data class DealPartner(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dealId: Long,
    val name: String,            // e.g. "Ahmed", "Rahim", "Salim"
    val amountInvested: Double,  // e.g. 50000.0
    val phone: String = ""
)
