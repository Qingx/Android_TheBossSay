package net.cd1369.tbs.android.ui.start

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import cn.wl.android.lib.core.PageParam
import com.advance.AdvanceSplash
import com.advance.AdvanceSplashListener
import com.advance.model.AdvanceError
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.TbsApp
import net.cd1369.tbs.android.data.db.ArticleDaoManager
import net.cd1369.tbs.android.data.db.BossDaoManager
import net.cd1369.tbs.android.data.db.LabelDaoManager
import net.cd1369.tbs.android.data.model.LabelModel
import net.cd1369.tbs.android.ui.dialog.ServicePrivacyDialog
import net.cd1369.tbs.android.ui.home.ArticleActivity
import net.cd1369.tbs.android.ui.home.HomeActivity
import java.util.concurrent.TimeUnit

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 16:28 2021/7/23
 * @desc
 */
class SplashActivity : FragmentActivity(), AdvanceSplashListener {

    private val rxPermission: RxPermissions by lazy {
        RxPermissions(this)
    }
    private var sdkId: String = ""

    private var hasAdShow = false
    private var hasService = false
//    private var hasLoadBoss = false

//    private var gotoLock = false

    //    private var bossList: List<BossInfoEntity> = mutableListOf()
    private var advanceSplash: AdvanceSplash? = null

    private val serviceDialog by lazy {
        ServicePrivacyDialog.showDialog(
            supportFragmentManager, "SplashActivity"
        )
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tryShowService()
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

                        doAgreeService()
                    }) {
                        hasService = true

                        HomeActivity.start(this)
                    }
            }
        } else {
            TbsApp.tryInitThree(applicationContext)

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

            doSecondUse()
        }
    }

    @SuppressLint("CheckResult")
    private fun doAgreeService() {
        TbsApi.boss().obtainBossLabels().onErrorReturn { mutableListOf() }
            .flatMap {
                it.add(0, LabelModel.empty)
                LabelDaoManager.getInstance(this).insertList(it)

                TbsApi.boss().obtainGuideBoss()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                GuideActivity.start(this, it)
            }, {
                HomeActivity.start(this)
            })
    }

    @SuppressLint("CheckResult")
    private fun doSecondUse() {
        TbsApi.boss().obtainBossLabels().onErrorReturn { mutableListOf() }
            .flatMap {
                it.add(0, LabelModel.empty)
                LabelDaoManager.getInstance(this).insertList(it)

                TbsApi.boss().obtainFollowBossList(-1L, false)
            }.flatMap {
                BossDaoManager.getInstance(this).insertList(it)

                TbsApi.boss().obtainTackArticle(-1L, PageParam.create(1, 10))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                DataConfig.get().tackTotalNum = it.total
                DataConfig.get().hasData = it.hasData()
                ArticleDaoManager.getInstance(this).insertList(it.records)

                if (WelActivity.tempId.isNullOrEmpty()) {
                    HomeActivity.start(this)
                    finish()
                } else {
                    doWelcome()
                }
            }, {
                if (WelActivity.tempId.isNullOrEmpty()) {
                    HomeActivity.start(this)
                    finish()
                } else {
                    doWelcome()
                }
            })
    }

    private fun doWelcome() {
        val intentHome = Intent(this, HomeActivity::class.java)
        intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val intentArticle = Intent(this, ArticleActivity::class.java)
        intentArticle.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intentArticle.putExtra("articleId", WelActivity.tempId)

        startActivities(arrayOf(intentHome, intentArticle))
        finish()
    }

    /**
     * 尝试加载boss信息
     */
//    private fun tryLoadBoos() {
//        if (bossList.isNullOrEmpty()) {
//            TbsApi.boss().obtainLabels().onErrorReturn { mutableListOf() }
//                .flatMap {
//                    it.add(0, LabelModel.empty)
//                    LabelDaoManager.getInstance(mContext).insertList(it)
//
//                    TbsApi.boss().obtainGuideBoss()
//                        .onErrorReturn { mutableListOf() }
//                }.observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    bossList = it
//                    hasLoadBoss = true
//
//                    tryLunchApp()
//                }) {
//
//                }
//        } else {
//            hasLoadBoss = true
//
//            tryLunchApp()
//        }
//    }

    /**
     * 尝试拉起app
     */
//    private fun tryLunchApp() {
//        if (hasService && hasLoadBoss && hasAdShow) {
//            if (gotoLock) {
//                return
//            }
//
//            gotoLock = true
//
//            val firstUse = DataConfig.get().firstUse
//
//            var tempId = WelActivity.tempId
//
//            if (firstUse && !bossList.isNullOrEmpty()) {
//                val guideBoss = bossList.filter { it.guide }
//                GuideActivity.start(this, ArrayList(guideBoss))
//                finish()
//            } else {
//                if (tempId.isNullOrEmpty()) {
//                    HomeActivity.start(this)
//                    finish()
//                } else {
//                    var intentHome = Intent(this, HomeActivity::class.java)
//                    intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//
//                    var intentArticle = Intent(this, ArticleActivity::class.java)
//                    intentArticle.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    intentArticle.putExtra("articleId", tempId)
//
//                    startActivities(arrayOf(intentHome, intentArticle))
//                }
//            }
//        }
//    }

    override fun onAdFailed(p0: AdvanceError) {
        hasAdShow = true

//        tryLunchApp()

        Log.e("OkHttp", p0.toString())
    }

    override fun onSdkSelected(id: String) {
        sdkId = id
    }

    override fun onAdClicked() {
        hasAdShow = true

        timerDelay(100) {
//            tryLunchApp()
        }
    }

    override fun onAdSkip() {
        hasAdShow = true

        timerDelay(100) {
//            tryLunchApp()
        }
    }

    override fun onAdTimeOver() {
        hasAdShow = true

        timerDelay(100) {
//            tryLunchApp()
        }
    }

    private fun timerDelay(time: Int, function: () -> Unit): Disposable {
        return Observable.timer(time.toLong(), TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                function.invoke()
            }) {

            }
    }

    override fun onAdShow() {
        group.isVisible = false
    }

    override fun onAdLoaded() {
    }


}