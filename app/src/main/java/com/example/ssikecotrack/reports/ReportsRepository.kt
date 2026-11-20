package com.example.ssikecotrack.reports

import android.content.Context
import com.example.ssikecotrack.EnergyCalculator
import com.example.ssikecotrack.data.AppDatabase
import com.example.ssikecotrack.data.ApplianceRepository
import kotlinx.coroutines.flow.first

interface ReportsRepository {
    suspend fun getReports(): List<EnergyReport>
}

class RoomReportsRepository(context: Context) : ReportsRepository {

    private val repository = ApplianceRepository(
        AppDatabase.getInstance(context.applicationContext).applianceDao()
    )

    override suspend fun getReports(): List<EnergyReport> {
        val appliances = repository.allAppliances.first()
        return appliances.map { appliance ->
            val dailyKwh = EnergyCalculator.calculateDailyKwh(
                powerRatingWatts = appliance.wattage * appliance.quantity,
                hoursUsedPerDay = appliance.hoursPerDay
            )
            val monthlyKwh = EnergyCalculator.calculateMonthlyKwh(dailyKwh)
            val monthlyCost = EnergyCalculator.calculateMonthlyCost(monthlyKwh)

            val label = if (appliance.quantity > 1) {
                "${appliance.name} (x${appliance.quantity})"
            } else {
                appliance.name
            }

            EnergyReport(
                applianceName = label,
                kwhUsed = monthlyKwh,
                cost = monthlyCost
            )
        }
    }
}