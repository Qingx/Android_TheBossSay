package net.cd1369.tbs.android.ui.start

import android.os.Bundle
import cn.wl.android.lib.ui.BaseActivity
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig

class StartActivity : BaseActivity() {
    override fun getLayoutResource(): Any {
        return R.layout.activity_start
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        if (DataConfig.get().firstUse) {
            GuideActivity.start(mActivity)
        } else SplashActivity.start(mActivity)

        mActivity?.finish()
    }
}