package com.sdb.ber

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.sdb.ber.databinding.ActHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SDBMainActivity : BaseActivity() {
    private lateinit var binding: ActHomeBinding

    private var actLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.homeBrowser.setOnClickListener {
            val sdba = getHomeInter()
            if (sdba != null && AppConfig.instance.isM()) {
                sdba.onClose {
                    startActivity(Intent(this, SDBBroActivity::class.java))
                }.show(this)
            } else {
                startActivity(Intent(this, SDBBroActivity::class.java))
            }
        }
        binding.homeM.setOnClickListener {
            val sdba = getHomeInter()
            if (sdba != null && AppConfig.instance.isM()) {
                sdba.onClose {
                    actLauncher?.launch(Intent(this, SDBMActivity::class.java))
                }.show(this)
            } else {
                actLauncher?.launch(Intent(this, SDBMActivity::class.java))
            }
        }
        binding.homeH.setOnClickListener {
            val sdba = getHomeInter()
            if (sdba != null && AppConfig.instance.isM()) {
                sdba.onClose {
                    actLauncher?.launch(Intent(this, SDBHActivity::class.java))
                }.show(this)
            } else {
                actLauncher?.launch(Intent(this, SDBHActivity::class.java))
            }
        }


        actLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                if (data != null) {
                    val url = data.getStringExtra("url") ?: return@registerForActivityResult
                    val intent = Intent(this, SDBBroActivity::class.java)
                    intent.putExtra("url", url)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadAd()
        loadInter()
    }


    private fun getHomeInter(): SDBa? {
        return SDBam.instance.get(SDBap.HOME)
    }

    private fun loadInter() {
        launch {
            if (SDBam.instance.get(SDBap.HOME) == null) {
                SDBam.instance.create(SDBap.HOME)
            }
        }
    }


    private fun loadAd() {
        launch {
            val sdba = SDBam.instance.create(SDBap.NATIVE)
            binding.adDef.isVisible = false
            if (sdba != null) {
                withContext(Dispatchers.Main) {
                    val adViewBind = binding.adView
                    adViewBind.adViewRoot.isVisible = true
                    adViewBind.adViewRoot.onGlobalLayout {
                        val adView = adViewBind.adView
                        sdba.showNav {
                            if (this == null) {
                                adView.visibility = View.GONE
                            } else {
                                adViewBind.adAction.text = this.callToAction
                                adViewBind.adTitle.text = this.headline
                                adViewBind.adDes.text = this.body
                                adView.adChoicesView = adViewBind.adChoices
                                adView.callToActionView = adViewBind.adAction
                                adView.imageView = adViewBind.adImage
                                adView.mediaView = adViewBind.adMedia
                                adView.iconView = adViewBind.adIcon
                                adView.headlineView = adViewBind.adTitle
                                adView.bodyView = adViewBind.adDes
                                Glide.with(this@SDBMainActivity).load(this.icon?.uri).into(adViewBind.adIcon)
                                adView.setNativeAd(this)
                                adView.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

}