package com.example.healthmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.data.HealthDatabase
import com.example.healthmate.data.HealthRecord
import com.example.healthmate.data.HealthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// totals shown on the Dashboard
data class DaySummary(
    val steps: Int = 0,
    val calories: Int = 0,
    val water: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HealthRepository

    // search text typed by the user
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // list of records (all, or filtered by the search text)
    val records: StateFlow<List<HealthRecord>>

    // today's totals for the Dashboard
    val todaySummary: StateFlow<DaySummary>

    init {
        val dao = HealthDatabase.getDatabase(application).healthRecordDao()
        repository = HealthRepository(dao)

        records = _searchQuery
            .flatMapLatest { query ->
                if (query.isBlank()) repository.allRecords else repository.search(query)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        todaySummary = repository.recordsForDate(todayDate())
            .map { list ->
                DaySummary(
                    steps = list.sumOf { it.steps },
                    calories = list.sumOf { it.calories },
                    water = list.sumOf { it.water }
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DaySummary())

        // dummy records the first time the app runs
        viewModelScope.launch { repository.seedDummyData() }
    }

    fun todayDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    fun addRecord(record: HealthRecord) {
        viewModelScope.launch { repository.add(record) }
    }

    fun updateRecord(record: HealthRecord) {
        viewModelScope.launch { repository.update(record) }
    }

    fun deleteRecord(record: HealthRecord) {
        viewModelScope.launch { repository.delete(record) }
    }

    suspend fun getRecord(id: Int): HealthRecord? = repository.getById(id)
}