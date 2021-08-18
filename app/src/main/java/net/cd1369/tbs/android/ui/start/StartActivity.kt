package net.cd1369.tbs.android.ui.start

import android.app.Activity
import android.os.Bundle
import cn.wl.android.lib.ui.BaseActivity
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig

class StartActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        SplashActivity.start(this)
        finish()
    }
}