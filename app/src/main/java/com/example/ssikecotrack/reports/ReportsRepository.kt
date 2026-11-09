package com.example.ssikecotrack.reports

interface ReportsRepository {
    suspend fun getReports(): List<EnergyReport>
}

// Placeholder data until Sean's part is ready
class FakeReportsRepository : ReportsRepository {
    override suspend fun getReports() = listOf(
        EnergyReport("Fridge", 42.5, 850.0),
        EnergyReport("Television", 18.0, 360.0),
        EnergyReport("Microwave", 9.2, 184.0)
    )
}