package com.sdb.ber.dt

import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.sdb.ber.dx.SDBa
import com.sdb.ber.sr.SDBap
import com.sdb.ber.SDBau
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SDBam {
    companion object {
        val instance: SDBam by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { SDBam() }
    }

    private lateinit var context: Context
    private var isLoad = hashMapOf<String, Boolean>()

    fun initialize(context: Context): SDBam {
        this.context = context
        try {
            MobileAds.initialize(context) {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    suspend fun create(key: String): SDBa? = withContext(Dispatchers.Main) {
        val lists = SDBap.getRequestLists(key)
        var ret: SDBa?
        for (req in lists) {
            if (req.id.isNotEmpty()) {
                ret = create(key, req)
                if (ret != null) {
                    return@withContext ret
                }
            }
        }
        return@withContext null
    }

    suspend fun creates(vararg key: String) = withContext(Dispatchers.Main) {
        val list = mutableListOf<SDBa>()
        for (k in key) {
            val ret = create(k)
            if (ret != null) {
                list.add(ret)
            }
        }
        return@withContext list
    }


    fun get(vararg key: String): SDBa? = getCache(*key, index = 0)


    private suspend fun create(key: String, sdbau: SDBau): SDBa? =
        withContext(Dispatchers.Main) {
            if (isLoad[key] == true) {
                return@withContext null
            }
            if (SDBap.hasCache(key)) {
                val sdba = get(key)
                if (sdba != null) {
                    return@withContext sdba
                }
            }
            isLoad[key] = true
            val ret = runCatching {
                when (sdbau.type) {
                    0 -> crateNativeAd(key, sdbau.id)
                    1 -> crateInterstitialAd(key, sdbau.id)
                    2 -> crateOpenAd(key, sdbau.id)
                    else -> crateInterstitialAd(key, sdbau.id)
                }
            }
            isLoad[key] = false

            if (ret.isSuccess) {
                val ad = ret.getOrNull()
                if (ad != null) {
                    return@withContext ad
                }
            }
            return@withContext null
        }


    private fun getCache(vararg key: String, index: Int): SDBa? {
        val count = key.size
        val sdba = SDBap.getCache(key[index])
        return sdba ?: if (index < count - 1) {
            getCache(*key, index = index + 1)
        } else {
            null
        }
    }

    private suspend fun crateNativeAd(key: String, id: String): SDBa {
        return suspendCancellableCoroutine {
            //广告需要ui线程上加载
            val adLoader = AdLoader.Builder(context, id)
                .forNativeAd { sdba ->
                    val nativeAd = SDBa(sdba)
                    SDBap.cacheList[key]?.onDestroy()
                    SDBap.cacheList[key] = nativeAd
                    it.resume(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        it.resumeWithException(Exception(p0.code.toString()))
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .build()
                )
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    private suspend fun crateInterstitialAd(key: String, id: String): SDBa {
        return suspendCancellableCoroutine {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, id, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    it.resumeWithException(Exception(adError.code.toString()))
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    val adba = SDBa(interstitialAd)
                    //之前有缓存的广告，先将之前的广告销毁
                    SDBap.cacheList[key]?.onDestroy()
                    SDBap.cacheList[key] = adba
                    it.resume(adba)
                }
            })
        }
    }

    private suspend fun crateOpenAd(key: String, id: String): SDBa {
        return suspendCancellableCoroutine {
            AppOpenAd.load(
                context,
                id,
                AdRequest.Builder().build(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        super.onAdFailedToLoad(adError)
                        it.resumeWithException(Exception(adError.code.toString()))
                    }

                    override fun onAdLoaded(p0: AppOpenAd) {
                        super.onAdLoaded(p0)
                        val sdba = SDBa(p0)
                        //之前有缓存的广告，先将之前的广告销毁
                        SDBap.cacheList[key]?.onDestroy()
                        SDBap.cacheList[key] = sdba
                        it.resume(sdba)
                    }
                }
            )
        }
    }

}