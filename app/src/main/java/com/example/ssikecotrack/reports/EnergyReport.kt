package com.example.ssikecotrack.reports

data class EnergyReport(
    val applianceName: String,
    val kwhUsed: Double,
    val cost: Double
)