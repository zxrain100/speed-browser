package com.sdb.ber.sq

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Process
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.google.firebase.FirebaseApp
import com.sdb.ber.dt.RCHelper
import com.sdb.ber.SDBSActivity
import com.sdb.ber.dt.SDBam


//speed2023315
class SDB : Application() {

    companion object {
        internal var instance: SDB? = null
        private val baseInstance: SDB by lazy { instance!! }
        val context: Context by lazy { baseInstance.applicationContext }
    }

    override fun attachBaseContext(base: Context?) {
        instance = this
        super.attachBaseContext(base)
    }


    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)

            if (getProcessName(this) != packageName) return

            //InstallReferrer
            IRHelper.instance.build(this)

            //初始化广告
            SDBam.instance.initialize(this)
            AppUtils.registerAppStatusChangedListener(object : Utils.OnAppStatusChangedListener {
                override fun onForeground(activity: Activity?) {
                    if (activity !is SDBSActivity) {
                        val intent = Intent(activity, SDBSActivity::class.java)
                        intent.putExtra("back", true)
                        activity?.startActivity(intent)
                    }
                }

                override fun onBackground(activity: Activity?) {
                }

            })
            RCHelper.instance.initAndFetchAndActivate()

        } catch (_: Exception) {
        }
    }


    private fun getProcessName(cxt: Context): String? {
        val pid = Process.myPid()
        val am = cxt.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (app in runningApps) {
            if (app.pid == pid) {
                return app.processName
            }
        }
        return null
    }
}