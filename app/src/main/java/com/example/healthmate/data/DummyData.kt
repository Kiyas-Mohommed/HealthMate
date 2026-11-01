package com.example.healthmate.data

object DummyData {
    val records = listOf(
        HealthRecord(date = "2026-10-25", steps = 6500, calories = 320, water = 1800),
        HealthRecord(date = "2026-10-26", steps = 8200, calories = 410, water = 2200),
        HealthRecord(date = "2026-10-27", steps = 4300, calories = 210, water = 1500),
        HealthRecord(date = "2026-10-28", steps = 10500, calories = 520, water = 2600),
        HealthRecord(date = "2026-10-29", steps = 7400, calories = 370, water = 2000),
        HealthRecord(date = "2026-10-30", steps = 9100, calories = 450, water = 2400),
        HealthRecord(date = "2026-10-31", steps = 5600, calories = 280, water = 1900)
    )

    // sample records only if the table is empty
    suspend fun seedIfEmpty(dao: HealthRecordDao) {
        if (dao.getCount() == 0) {
            dao.insertAll(records)
        }
    }
}