package net.cd1369.tbs.android.ui.start

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import cn.wl.android.lib.ui.BaseActivity
import com.blankj.utilcode.util.PhoneUtils
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.database.BossInfoDaoManager
import net.cd1369.tbs.android.data.database.BossLabelDaoManager
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.ui.home.HomeActivity
import net.cd1369.tbs.android.util.LabelManager

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

    @SuppressLint("SetTextI18n", "MissingPermission")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        val firstUse = DataConfig.get().firstUse

        Log.e("OkHttp", PhoneUtils.getIMEI())

        LabelManager.obtainLabels()
            .flatMap {
                if (!it.isNullOrEmpty()) {
                    it.add(0, BossLabelEntity.empty)
                }

                TbsApi.boss().obtainAllBoss(DataConfig.get().updateTime)
                    .onErrorReturn { mutableListOf() }
            }.bindDefaultSub {
                if (!it.isNullOrEmpty()) {
                    BossInfoDaoManager.getInstance().insertList(it)
                }

                if (firstUse && !it.isNullOrEmpty()) {
                    val filter = it.filter { it.guide }
                    GuideActivity.start(mActivity, ArrayList(filter))
                } else HomeActivity.start(mActivity)
            }
    }
}