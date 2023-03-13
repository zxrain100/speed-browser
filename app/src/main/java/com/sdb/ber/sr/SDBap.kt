package com.sdb.ber.sr

import android.util.Base64
import com.sdb.ber.dx.SDBa
import com.sdb.ber.SDBau
import com.sdb.ber.dt.RCHelper
import org.json.JSONObject


object SDBap {
    const val INDEX = "sdb_inter_s"
    const val HOME = "sdb_inter_h"
    const val NATIVE = "sdb_native"

    val cacheList = HashMap<String, SDBa>()
    fun hasCache(key: String): Boolean {
        synchronized(cacheList) {
            if (cacheList.isEmpty()) return false
            val cache = cacheList[key] ?: return false
            return if (cache.isAva()) {
                true
            } else {
                cache.onDestroy()
                cacheList.remove(key)
                false
            }
        }
    }

    fun getCache(key: String): SDBa? {
        synchronized(cacheList) {
            if (cacheList.isEmpty()) return null
            val cache = cacheList[key] ?: return null
            return if (!cache.isAva()) {
                cache.onDestroy()
                null
            } else {
                cache
            }
        }
    }

    fun getRequestLists(sk: String): List<SDBau> {
        try {
            val s = RCHelper.instance.getSDBCfg()
            val json = JSONObject(String(Base64.decode(s.toByteArray(), Base64.DEFAULT)))
//            val mode = json.getBoolean("sdb_mode")
//            AppConfig.instance.setSafeMode(mode)
            val jsonArray = json.getJSONArray("sdb_config")

            val adReqList = mutableListOf<SDBau>()
            for (i in 0 until jsonArray.length()) {
                val obj: JSONObject = jsonArray.getJSONObject(i)
                val key = obj.getString("sdb_key")
                val jsonArray2 = obj.getJSONArray("sdb_ids")
                val au: MutableList<SDBau> = mutableListOf()
                for (j in 0 until jsonArray2.length()) {
                    val obj2: JSONObject = jsonArray2.getJSONObject(j)
                    val id = obj2.getString("sdb_id")
                    val priority = obj2.getInt("sdb_priority")
                    val t = when (obj2.optString("sdb_type")) {
                        "nav" -> 0
                        "inter" -> 1
                        "open" -> 2
                        else -> 1
                    }
                    au.add(SDBau(id, priority, t))
                }
                au.sortBy { -it.priority }
                if (key == sk) {
                    adReqList.addAll(au)
                }
            }
            return adReqList
        } catch (e: Exception) {
            //e.printStackTrace()
            return mutableListOf()
        }
    }

}
