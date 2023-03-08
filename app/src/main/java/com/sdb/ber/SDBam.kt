package com.sdb.ber

import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
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


    private suspend fun create(key: String, plbau: SDBau): SDBa? =
        withContext(Dispatchers.Main) {
            if (isLoad[key] == true) {
                return@withContext null
            }
            if (SDBap.hasCache(key)) {
                val PLBA = get(key)
                if (PLBA != null) {
                    return@withContext PLBA
                }
            }
            isLoad[key] = true
            val ret = runCatching {
                when (plbau.type) {
                    0 -> crateNativeAd(key, plbau.id)
                    1 -> crateInterstitialAd(key, plbau.id)
                    else -> crateInterstitialAd(key, plbau.id)
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
        val plba = SDBap.getCache(key[index])
        return plba ?: if (index < count - 1) {
            getCache(*key, index = index + 1)
        } else {
            null
        }
    }

    private suspend fun crateNativeAd(key: String, id: String): SDBa {
        return suspendCancellableCoroutine {
            //广告需要ui线程上加载
            val adLoader = AdLoader.Builder(context, id)
                .forNativeAd { plba ->
                    val nativeAd = SDBa(plba)
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
                    val plba = SDBa(interstitialAd)
                    //之前有缓存的广告，先将之前的广告销毁
                    SDBap.cacheList[key]?.onDestroy()
                    SDBap.cacheList[key] = plba
                    it.resume(plba)
                }
            })
        }
    }

}