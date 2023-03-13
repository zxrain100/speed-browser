package com.sdb.ber.dx

import androidx.room.*
import com.sdb.ber.sn.SDBMark

@Dao
abstract class SDBMarkDao {

    @Query("SELECT * FROM ${SDBMark.Table.TABLE_NAME}")
    abstract fun getAll(): MutableList<SDBMark>

    @Query("SELECT * FROM ${SDBMark.Table.TABLE_NAME} WHERE sdb_url = :url")
    abstract fun getByUrl(url: String): SDBMark?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addPage(page: SDBMark)

    @Delete
    abstract fun deletePage(page: SDBMark)
}