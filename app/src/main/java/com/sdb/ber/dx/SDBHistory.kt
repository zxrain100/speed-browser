package com.sdb.ber.dx

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = SDBHistory.Table.TABLE_NAME)
data class SDBHistory(
    /**
     * 页面url
     */
    @ColumnInfo(name = "sdb_url")
    @PrimaryKey
    val url: String,
    /**
     * 页面标题
     */
    @ColumnInfo(name = "sdb_title")
    val title: String,

    /**
     * 添加时间/浏览时间
     */
    @ColumnInfo(name = "sdb_time")
    val time: Long

) {
    object Table {
        const val TABLE_NAME = "sdb_history"
    }
}