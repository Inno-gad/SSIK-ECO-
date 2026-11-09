package com.example.ssikecotrack.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed class ReportsState {
    object Loading : ReportsState()
    object Empty : ReportsState()
    data class Success(
        val reports: List<EnergyReport>,
        val totalKwh: Double,
        val totalCost: Double
    ) : ReportsState()
    data class Error(val message: String) : ReportsState()
}

class ReportsViewModel(
    private val repository: ReportsRepository = FakeReportsRepository()
) : ViewModel() {

    private val _state = MutableLiveData<ReportsState>()
    val state: LiveData<ReportsState> = _state

    init {
        load()
    }

    fun load() {
        _state.value = ReportsState.Loading
        viewModelScope.launch {
            try {
                val data = repository.getReports()
                _state.value = if (data.isEmpty()) {
                    ReportsState.Empty
                } else {
                    ReportsState.Success(
                        data,
                        data.sumOf { it.kwhUsed },
                        data.sumOf { it.cost }
                    )
                }
            } catch (e: Exception) {
                _state.value = ReportsState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}