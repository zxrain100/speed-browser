package com.sdb.ber.sr

import android.net.Uri
import android.util.TypedValue
import com.sdb.ber.sq.SDB

object SDBUtils {

    fun dip2px(dp: Float): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        SDB.context.resources.displayMetrics
    )

    fun dip2px(dp: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        SDB.context.resources.displayMetrics
    ).toInt()

    /**
     * 获取网站图标
     */
    fun getIconUrl(url: String): String {
        val host = Uri.parse(url).host
        return if (url.startsWith("https")) {
            "https://$host/favicon.ico"
        } else {
            "http://$host/favicon.ico"
        }
    }


}