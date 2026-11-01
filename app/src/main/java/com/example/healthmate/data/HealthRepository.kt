package com.example.healthmate.data

import kotlinx.coroutines.flow.Flow

class HealthRepository(private val dao: HealthRecordDao) {
    val allRecords: Flow<List<HealthRecord>> = dao.getAllRecords()

    fun search(query: String): Flow<List<HealthRecord>> = dao.searchByDate(query)

    fun recordsForDate(date: String): Flow<List<HealthRecord>> = dao.getRecordsByDate(date)

    suspend fun getById(id: Int): HealthRecord? = dao.getRecordById(id)

    suspend fun add(record: HealthRecord) = dao.insert(record)

    suspend fun update(record: HealthRecord) = dao.update(record)

    suspend fun delete(record: HealthRecord) = dao.delete(record)

    suspend fun seedDummyData() = DummyData.seedIfEmpty(dao)
}