package com.sdb.ber

interface WebCallback {

    fun onWebStarted(url: String)

    fun onWebFinish(url: String)

    fun onWebProgress(progress: Int)

}