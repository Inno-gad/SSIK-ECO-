package com.example.ssikecotrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "appliances")
data class ApplianceEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val wattage: Double,
    val hoursPerDay: Double,
    val quantity: Int = 1,
    val category: String? = null,
    val room: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)