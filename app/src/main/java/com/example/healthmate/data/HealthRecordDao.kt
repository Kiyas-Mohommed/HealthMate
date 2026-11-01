package com.example.healthmate.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {
    // create
    @Insert
    suspend fun insert(record: HealthRecord)

    @Insert
    suspend fun insertAll(records: List<HealthRecord>)

    // read
    @Query("SELECT * FROM health_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE id = :id")
    suspend fun getRecordById(id: Int): HealthRecord?

    @Query("SELECT * FROM health_records WHERE date = :date")
    fun getRecordsByDate(date: String): Flow<List<HealthRecord>>

    // search
    @Query("SELECT * FROM health_records WHERE date LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchByDate(query: String): Flow<List<HealthRecord>>

    // update
    @Update
    suspend fun update(record: HealthRecord)

    // delete
    @Delete
    suspend fun delete(record: HealthRecord)

    // used later to add dummy data only once
    @Query("SELECT COUNT(*) FROM health_records")
    suspend fun getCount(): Int
}