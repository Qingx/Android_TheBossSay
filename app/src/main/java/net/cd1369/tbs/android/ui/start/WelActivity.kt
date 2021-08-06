package net.cd1369.tbs.android.ui.start

import android.os.Bundle
import cn.wl.android.lib.ui.BaseActivity
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 11:56 2021/8/6
 * @desc 网页启动界面
 */
class WelActivity : BaseActivity() {

    private var tempId: String = ""

    override fun getLayoutResource(): Any {
        return R.layout.activity_start
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        tempId = intent.getStringExtra("id") ?: ""
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        SplashActivity.start(mActivity)

        mActivity?.finish()
    }

}