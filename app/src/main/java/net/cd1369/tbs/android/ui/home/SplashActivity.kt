package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import cn.wl.android.lib.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*
import net.cd1369.tbs.android.R

class SplashActivity : BaseActivity() {
    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, SplashActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_splash
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        countdown(5) {
            text_time.text = "${it}s"

            if (it <= 0) {
                HomeActivity.start(mActivity)
                mActivity?.finish()
            }
        }
    }
}