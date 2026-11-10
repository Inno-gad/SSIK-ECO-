package com.example.ssikecotrack

object EnergyCalculator {

    // Default rate per kWh (can be adjusted if needed)
    const val COST_PER_KWH: Double = 25.0

    fun calculateDailyKwh(powerRatingWatts: Double, hoursUsedPerDay: Double): Double {
        return (powerRatingWatts * hoursUsedPerDay) / 1000.0
    }

    fun calculateMonthlyKwh(dailyKwh: Double): Double {
        return dailyKwh * 30.0
    }

    fun calculateDailyCost(dailyKwh: Double, costPerKwh: Double = COST_PER_KWH): Double {
        return dailyKwh * costPerKwh
    }

    fun calculateMonthlyCost(monthlyKwh: Double, costPerKwh: Double = COST_PER_KWH): Double {
        return monthlyKwh * costPerKwh
    }

    fun classifyConsumption(dailyKwh: Double): String {
        return when {
            dailyKwh <= 2.0 -> "Low Consumption"
            dailyKwh <= 7.0 -> "Moderate Consumption"
            else -> "High Consumption"
        }
    }
    
/**
     * Helper to compute total monthly consumption across a collection of appliances.
     * Pass a list of Pair(powerRatingWatts, hoursUsedPerDay).
     */
    fun calculateTotalMonthlyKwh(appliances: List<Pair<Double, Double>>): Double {
        return appliances.sumOf { (watts, hours) ->
            calculateMonthlyKwh(calculateDailyKwh(watts, hours))
        }
    }

}
