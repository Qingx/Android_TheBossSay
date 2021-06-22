package net.cd1369.tbs.android.util

import android.view.View
import cn.wl.android.lib.utils.Times

/**
 * Created by Xiang on 2021/06/22 10:39
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class DoubleClickListener : View.OnClickListener {
    private var lastTime: Long = 0

    companion object {
        const val DoubleTime: Long = 1000
    }

    override fun onClick(v: View?) {
        val currentTime = Times.current()
        if (currentTime - lastTime < DoubleTime) {
            onDoubleClick(v!!)
        }
        lastTime = currentTime
    }

    abstract fun onDoubleClick(v: View)
}