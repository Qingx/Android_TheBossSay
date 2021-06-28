package net.cd1369.tbs.android.ui.start

import android.os.Bundle
import cn.wl.android.lib.ui.BaseActivity
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.ui.home.HomeActivity
import net.cd1369.tbs.android.ui.home.MainActivity

class StartActivity : BaseActivity() {
    override fun getLayoutResource(): Any {
        return R.layout.activity_start
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        timerDelay(600) {
            HomeActivity.start(mActivity)
        }
    }
}