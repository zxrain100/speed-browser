package com.sdb.ber

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.sdb.ber.sr.SDBUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@Suppress("DEPRECATION")
open class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    //是否在前台
    protected var isFront = false

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window?.decorView?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = Color.TRANSPARENT

        //设置状态栏模式
        val wc = WindowCompat.getInsetsController(window, window.decorView)
        wc.isAppearanceLightStatusBars = false

        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        isFront = true
    }

    override fun onPause() {
        super.onPause()
        isFront = false
    }

    internal fun View.setStatusBar(): Int {
        val sH = getStatusBarH()
        val newPaddingTop = paddingTop + sH
        setPadding(paddingLeft, newPaddingTop, paddingRight, bottom)
        return layoutParams.apply {
            height += sH
        }.apply {
            layoutParams = this
        }.run { height }
    }
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    private fun getStatusBarH():Int{
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = runCatching { resources.getDimensionPixelSize(resourceId) }.getOrDefault(0)
        }

        if (result == 0) {
            result = SDBUtils.dip2px(24)
        }

        return result
    }

    inline fun View.onGlobalLayout(crossinline callback: () -> Unit) {
        this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                callback()
            }
        })
    }
}