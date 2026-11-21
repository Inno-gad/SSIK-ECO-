package com.example.ssikecotrack.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ApplianceRepository(
    private val dao: ApplianceDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val collection get() = firestore.collection("appliances")

    val allAppliances: Flow<List<ApplianceEntity>> = dao.getAll()
    val applianceCount: Flow<Int> = dao.getCount()
    val totalDailyKwh: Flow<Double> = dao.getTotalDailyKwh()

    suspend fun addAppliance(appliance: ApplianceEntity) {
        val item = appliance.copy(updatedAt = System.currentTimeMillis(), synced = false)
        dao.insert(item)
        pushToCloud(item)
    }

    suspend fun updateAppliance(appliance: ApplianceEntity) {
        val item = appliance.copy(updatedAt = System.currentTimeMillis(), synced = false)
        dao.update(item)
        pushToCloud(item)
    }

    suspend fun deleteAppliance(appliance: ApplianceEntity) {
        dao.delete(appliance)
        collection.document(appliance.id).delete()
    }

    suspend fun getAppliance(id: String): ApplianceEntity? = dao.getById(id)

    // Upload one appliance to Firestore; mark it synced in Room once it succeeds
    private fun pushToCloud(appliance: ApplianceEntity) {
        val data = hashMapOf(
            "id" to appliance.id,
            "name" to appliance.name,
            "wattage" to appliance.wattage,
            "hoursPerDay" to appliance.hoursPerDay,
            "quantity" to appliance.quantity,
            "category" to appliance.category,
            "room" to appliance.room,
            "updatedAt" to appliance.updatedAt
        )
        collection.document(appliance.id).set(data)
            .addOnSuccessListener {
                Log.d("ApplianceSync", "Uploaded ${appliance.name} to Firestore")
                scope.launch { dao.markSynced(appliance.id, appliance.updatedAt) }
            }
            .addOnFailureListener { e ->
                Log.e("ApplianceSync", "Upload failed for ${appliance.name}", e)
            }
    }

    // Retry anything that has not reached the cloud yet (call on app/screen start)
    fun syncPending() {
        scope.launch {
            dao.getUnsynced().forEach { pushToCloud(it) }
        }
    }

    // Bring cloud data into Room; the newer updatedAt wins
    fun pullFromCloud() {
        collection.get().addOnSuccessListener { snapshot ->
            scope.launch {
                for (doc in snapshot.documents) {
                    val remote = ApplianceEntity(
                        id = doc.getString("id") ?: doc.id,
                        name = doc.getString("name") ?: "",
                        wattage = doc.getDouble("wattage") ?: 0.0,
                        hoursPerDay = doc.getDouble("hoursPerDay") ?: 0.0,
                        quantity = (doc.getLong("quantity") ?: 1L).toInt(),
                        category = doc.getString("category"),
                        room = doc.getString("room"),
                        updatedAt = doc.getLong("updatedAt") ?: 0L,
                        synced = true
                    )
                    val local = dao.getById(remote.id)
                    if (local == null || remote.updatedAt > local.updatedAt) {
                        dao.insert(remote)
                    }
                }
            }
        }
    }
}