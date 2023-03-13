package com.sdb.ber.sr

interface WebCallback {

    fun onWebStarted(url: String)

    fun onWebFinish(url: String)

    fun onWebProgress(progress: Int)

}