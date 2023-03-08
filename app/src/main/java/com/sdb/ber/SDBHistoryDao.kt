package com.sdb.ber

import androidx.room.*

@Dao
abstract class SDBHistoryDao {

    @Query("SELECT * FROM ${SDBHistory.Table.TABLE_NAME}")
    abstract fun getAll(): MutableList<SDBHistory>

    @Query("SELECT * FROM ${SDBHistory.Table.TABLE_NAME} WHERE sdb_url = :url")
    abstract fun getByUrl(url: String): SDBHistory?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addPage(page: SDBHistory)

    @Delete
    abstract fun deletePage(page: SDBHistory)
}