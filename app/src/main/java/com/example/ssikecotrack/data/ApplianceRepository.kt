package com.example.ssikecotrack.data

import kotlinx.coroutines.flow.Flow

class ApplianceRepository(private val dao: ApplianceDao) {

    val allAppliances: Flow<List<ApplianceEntity>> = dao.getAll()
    val applianceCount: Flow<Int> = dao.getCount()
    val totalDailyKwh: Flow<Double> = dao.getTotalDailyKwh()

    suspend fun addAppliance(appliance: ApplianceEntity) =
        dao.insert(appliance.copy(updatedAt = System.currentTimeMillis(), synced = false))

    suspend fun updateAppliance(appliance: ApplianceEntity) =
        dao.update(appliance.copy(updatedAt = System.currentTimeMillis(), synced = false))

    suspend fun deleteAppliance(appliance: ApplianceEntity) = dao.delete(appliance)

    suspend fun getAppliance(id: String): ApplianceEntity? = dao.getById(id)
}