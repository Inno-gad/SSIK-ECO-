package com.example.ssikecotrack.reports

import com.example.ssikecotrack.EnergyCalculator

interface ReportsRepository {
    suspend fun getReports(): List<EnergyReport>
}

// Placeholder data wired to Sean's EnergyCalculator until Room DB is ready
class FakeReportsRepository : ReportsRepository {
    override suspend fun getReports() = listOf(
        EnergyReport("Fridge", 42.5, EnergyCalculator.calculateMonthlyCost(42.5)),
        EnergyReport("Television", 18.0, EnergyCalculator.calculateMonthlyCost(18.0)),
        EnergyReport("Microwave", 9.2, EnergyCalculator.calculateMonthlyCost(9.2))
    )
}
}
