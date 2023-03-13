package com.sdb.ber.dt

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.sdb.ber.R

class RCHelper {
    companion object {
        val instance: RCHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { RCHelper() }
    }

    private lateinit var config: FirebaseRemoteConfig

    fun getSDBCfg() = config.getString("sdb_cfg")

    fun initAndFetchAndActivate() {
        config = FirebaseRemoteConfig.getInstance()
        config.setDefaultsAsync(R.xml.remote_config_default)
        config.fetchAndActivate()
    }

}