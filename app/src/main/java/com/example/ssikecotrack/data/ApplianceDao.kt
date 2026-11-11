package com.example.ssikecotrack.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplianceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appliance: ApplianceEntity)

    @Update
    suspend fun update(appliance: ApplianceEntity)

    @Delete
    suspend fun delete(appliance: ApplianceEntity)

    @Query("SELECT * FROM appliances ORDER BY name ASC")
    fun getAll(): Flow<List<ApplianceEntity>>

    @Query("SELECT * FROM appliances WHERE id = :id")
    suspend fun getById(id: String): ApplianceEntity?

    // For the dashboard
    @Query("SELECT COUNT(*) FROM appliances")
    fun getCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(wattage * hoursPerDay * quantity) / 1000.0, 0.0) FROM appliances")
    fun getTotalDailyKwh(): Flow<Double>

    // For Firebase sync later
    @Query("SELECT * FROM appliances WHERE synced = 0")
    suspend fun getUnsynced(): List<ApplianceEntity>
}