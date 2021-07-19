package net.cd1369.tbs.android.ui.start

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import cn.wl.android.lib.data.core.HttpConfig
import cn.wl.android.lib.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.ui.home.HomeActivity

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
        countdown(3) {
            text_time.text = "${it}s"

            if (it <= 0) {
                HomeActivity.start(mActivity)
                mActivity?.finish()
            }
        }

//        val loginStatus = UserConfig.get().loginStatus

//        if (loginStatus) {
//            TbsApi.user().obtainRefreshUser().bindDefaultSub {
//                HttpConfig.saveToken(it.token)
//
//                UserConfig.get().userEntity = it.userInfo
//            }
//        } else {
//            val tempId = DataConfig.get().tempId
//            TbsApi.user().obtainTempLogin(tempId).bindDefaultSub {
//                HttpConfig.saveToken(it.token)
//
//                UserConfig.get().userEntity = it.userInfo
//            }
//        }
    }
}