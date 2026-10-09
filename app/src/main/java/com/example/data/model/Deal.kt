package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deals")
data class Deal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,             // e.g. "Old Building Demolition", "Madanapalle Bunglow"
    val location: String,         // e.g. "Madanapalle", "Old Town"
    val date: String,             // e.g. "25 Aug 2026"
    val purchasePrice: Double,    // e.g. 100000.0
    val isCompleted: Boolean = false, // false = 🟡 IN PROGRESS, true = 🟢 COMPLETE
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
