package com.sdb.ber

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd

@Suppress("unused", "MemberVisibilityCanBePrivate")
class SDBa {

    private var exTime: Long = 0
    private var nav: NativeAd? = null
    private var inter: InterstitialAd? = null
    private var closeListener: (() -> Unit)? = null
    private var isAva = true
    private var type: Int = 0

    constructor(inter: InterstitialAd) {
        this.type = 0
        this.inter = inter
        exTime = System.currentTimeMillis() + 60 * 60 * 1000
    }

    constructor(nat: NativeAd) {
        this.type = 1
        this.nav = nat
        exTime = System.currentTimeMillis() + 60 * 60 * 1000
    }

    private var mFullScreenContentCallback: FullScreenContentCallback =
        object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                closeListener?.invoke()
                onDestroy()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                closeListener?.invoke()
                onDestroy()
            }

            override fun onAdShowedFullScreenContent() {
                inter = null
            }
        }

    /**
     * 广告关闭时回调
     */
    fun onClose(callBack: () -> Unit) = apply {
        this.closeListener = callBack
    }

    fun showNav(nav: NativeAd?.() -> Unit) {
        this.nav.let {
            nav.invoke(it)
        }
    }

    fun show(activity: Activity) {
        try {
            if (inter == null) {
                closeListener?.invoke()
            }
            inter?.fullScreenContentCallback = mFullScreenContentCallback
            inter?.show(activity)

        } catch (_: Exception) {
            closeListener?.invoke()
        }
    }

    /**
     * 广告是否可用，广告展示之后调用onDestroy置为不可用
     */
    fun isAva(): Boolean {
        when (type) {
            1 -> if (nav == null) {
                return false
            }
            0 -> if (inter == null) {
                return false
            }
        }
        return isAva && (System.currentTimeMillis() <= exTime)
    }

    fun onDestroy() {
        isAva = false
        nav?.destroy()
        inter = null
        nav = null
    }

}