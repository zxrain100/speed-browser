package com.sdb.ber

import androidx.room.*

@Dao
abstract class SDBMarkDao {

    @Query("SELECT * FROM ${SDBMark.Table.TABLE_NAME}")
    abstract fun getAll(): MutableList<SDBMark>

    @Query("SELECT * FROM ${SDBMark.Table.TABLE_NAME} WHERE plb_url = :url")
    abstract fun getByUrl(url: String): SDBMark?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addPage(page: SDBMark)

    @Delete
    abstract fun deletePage(page: SDBMark)
}