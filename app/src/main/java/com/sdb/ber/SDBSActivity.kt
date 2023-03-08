package com.sdb.ber

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import com.sdb.ber.databinding.ActStartBinding
import kotlinx.coroutines.*

class SDBSActivity : BaseActivity() {

    private var isBack = false
    private var anim: ValueAnimator? = null

    private lateinit var binding: ActStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isBack = intent.getBooleanExtra("back", false)
        start()
    }


    private fun start() {
        anim = ValueAnimator.ofInt(0, 90)
        anim?.duration = 9 * 1000L
        anim?.addUpdateListener {
            binding.loadBar.progress = it.animatedValue as Int
        }
        anim?.start()

        launch {
            withTimeoutOrNull(10100) {
                SDBam.instance.creates(SDBap.INDEX, SDBap.HOME, SDBap.NATIVE)
            }
            withContext(Dispatchers.Main) {
                anim?.cancel()
                val progress = binding.loadBar.progress
                if (progress < 100) {
                    val animator = ValueAnimator.ofInt(progress, 100)
                    animator.duration = 500L
                    animator.addUpdateListener {
                        val v = it.animatedValue as Int
                        binding.loadBar.progress = v
                        if (v >= 100) {
                            showAd()
                        }
                    }
                    animator.start()
                } else {
                    showAd()
                }
            }
        }
    }

    private fun showAd() {
        val ad = SDBam.instance.get(SDBap.INDEX)
        if (ad != null && isFront) {
            ad.onClose {
                if (!isBack) {
                    startToMain()
                }
                finish()
            }
            ad.show(this)
        } else {
            if (!isBack) {
                startToMain()
            }
            finish()
        }
    }

    private fun startToMain() {
        if (AppConfig.instance.isM()) {
            startActivity(Intent(this, SDBMainActivity::class.java))
        } else {
            startActivity(Intent(this, SDBBroActivity::class.java))
        }
    }

}