package net.cd1369.tbs.android.ui.start

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import cn.wl.android.lib.ui.BaseActivity
import com.advance.AdvanceSplash
import com.advance.AdvanceSplashListener
import com.advance.model.AdvanceError
import kotlinx.android.synthetic.main.activity_splash.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.TbsApp
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.ui.dialog.ServicePrivacyDialog
import net.cd1369.tbs.android.ui.home.ArticleActivity
import net.cd1369.tbs.android.ui.home.HomeActivity
import net.cd1369.tbs.android.util.LabelManager

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 16:28 2021/7/23
 * @desc
 */
class SplashActivity : BaseActivity(), AdvanceSplashListener {

    private var sdkId: String = ""

    private var hasAdShow = false
    private var hasService = false
    private var hasLoadBoss = false

    private var gotoLock = false

    private var bossList: List<BossInfoEntity> = mutableListOf()
    private var advanceSplash: AdvanceSplash? = null

    private val serviceDialog by lazy {
        ServicePrivacyDialog.showDialog(
            supportFragmentManager, "SplashActivity")
    }

    companion object {

        /**
         * 预申请动态权限
         */
        private val mPer = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        /**
         * 启动界面
         * @param context Context?
         */
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

    override fun initViewCreated(savedInstanceState: Bundle?) {
        tryShowService()

        LabelManager.obtainLabels()
            .flatMap {
                if (!it.isNullOrEmpty()) {
                    it.add(0, BossLabelEntity.empty)
                }

                TbsApi.boss().obtainGuideBoss()
                    .onErrorReturn { mutableListOf() }
            }.bindDefaultSub {
                bossList = it
                hasLoadBoss = true

                tryLunchApp()
            }
    }

    /**
     * 尝试显示服务协议
     */
    private fun tryShowService() {
        if (DataConfig.get().isNeedService) {
            hasAdShow = true

            serviceDialog.doConfirm = Runnable {
                DataConfig.get().isNeedService = false
                TbsApp.tryInitThree(applicationContext)

                rxPermission.request(*mPer)
                    .subscribe({
                        hasService = true

                        timerDelay(100) {
                            tryLunchApp()
                        }
                    }) {
                        hasService = true

                        timerDelay(100) {
                            tryLunchApp()
                        }
                    }
            }
        } else {
            hasService = true
            //开屏初始化；adContainer为广告容器，skipView不需要自定义可以为null
            //开屏初始化；adContainer为广告容器，skipView不需要自定义可以为null
            advanceSplash = AdvanceSplash(this, Const.LUNCH_ID, fl_wel, null)
            //必须：设置开屏核心回调事件的监听器。
            //必须：设置开屏核心回调事件的监听器。
            advanceSplash?.setAdListener(this)
            // 必须：请求策略并请求和展示广告，如果targetSDKVersion >= 23，需要申请好权限,
            // 如果您的App没有适配到Android6.0（即targetSDKVersion < 23）或者已经提前申请权限，
            // 那么只需要在这里直接调用loadAd方法。
            advanceSplash?.loadStrategy()
        }
    }

    /**
     * 尝试拉起app
     */
    private fun tryLunchApp() {
        if (hasService && hasLoadBoss && hasAdShow) {
            if (gotoLock) {
                return
            }

            gotoLock = true

            val firstUse = DataConfig.get().firstUse

            var tempId = WelActivity.tempId

            if (firstUse && !bossList.isNullOrEmpty()) {
                val guideBoss = bossList.filter { it.guide }
                GuideActivity.start(mActivity, ArrayList(guideBoss))
                mActivity?.finish()
            } else {
                if (tempId.isNullOrEmpty()) {
                    HomeActivity.start(mActivity)
                    mActivity?.finish()
                } else {
                    var intentHome = Intent(mActivity, HomeActivity::class.java)
                    intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    var intentArticle = Intent(mActivity, ArticleActivity::class.java)
                    intentArticle.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intentArticle.putExtra("articleId", tempId)

                    mActivity.startActivities(arrayOf(intentHome, intentArticle))
                }
            }
        }
    }

    override fun onAdFailed(p0: AdvanceError) {
        hasAdShow = true

        tryLunchApp()

        Log.e("OkHttp", p0.toString())
    }

    override fun onSdkSelected(id: String) {
        sdkId = id
    }

    override fun onAdClicked() {
        hasAdShow = true

        timerDelay(100) {
            tryLunchApp()
        }
    }

    override fun onAdSkip() {
        hasAdShow = true

        timerDelay(100) {
            tryLunchApp()
        }
    }

    override fun onAdTimeOver() {
        hasAdShow = true

        timerDelay(100) {
            tryLunchApp()
        }
    }

    override fun onAdShow() {
        iv_wel.isVisible = false
    }

    override fun onAdLoaded() {
    }

}