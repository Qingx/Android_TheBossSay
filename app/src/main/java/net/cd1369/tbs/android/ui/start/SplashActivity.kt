package net.cd1369.tbs.android.ui.start

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.core.PageParam
import cn.wl.android.lib.data.core.HttpConfig
import com.advance.AdvanceSplash
import com.advance.AdvanceSplashListener
import com.advance.model.AdvanceError
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.*
import net.cd1369.tbs.android.data.cache.CacheConfig
import net.cd1369.tbs.android.data.entity.TokenEntity
import net.cd1369.tbs.android.ui.dialog.ServicePrivacyDialog
import net.cd1369.tbs.android.ui.home.WebArticleActivity
import net.cd1369.tbs.android.ui.home.HomeActivity
import java.util.concurrent.TimeUnit

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 16:28 2021/7/23
 * @desc
 */
class SplashActivity : FragmentActivity(), AdvanceSplashListener {
    private var sdkId: String = ""

    private var hasAdShow = false
    private var hasService = false
    private var advanceSplash: AdvanceSplash? = null

    private val serviceDialog by lazy {
        ServicePrivacyDialog.showDialog(
            supportFragmentManager, "SplashActivity"
        )
    }

    companion object {
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
                hasService = true
                DataConfig.get().isNeedService = false
                TbsApp.tryInitThree(applicationContext)

                doAgreeService()
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
        TbsApi.boss().obtainBossLabels()
            .onErrorReturn { mutableListOf() }
            .flatMap {
                CacheConfig.insertLabelList(it)
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
        TbsApi.boss().obtainBossLabels()
            .onErrorReturn { mutableListOf() }
            .flatMap {
                CacheConfig.insertLabelList(it)

                TbsApi.boss().obtainFollowBossList("-1", false)
            }.onErrorReturn { mutableListOf() }
            .flatMap {
                CacheConfig.insertBossList(it)

                TbsApi.boss().obtainTackArticle("-1", PageParam.create(1, 10))
            }
            .onErrorReturn { Page.empty() }
            .flatMap {
                CacheConfig.insertArticle(it)

                TbsApi.user().obtainRefreshUser()
            }
            .onErrorReturn {
                TokenEntity(HttpConfig.getToken(), UserConfig.get().userEntity)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .timeout(8, TimeUnit.SECONDS)
            .subscribe {
                HttpConfig.saveToken(it.token)
                UserConfig.get().userEntity = it.userInfo

                if (WelActivity.tempId.isNullOrEmpty()) {
                    HomeActivity.start(this)
                    finish()
                } else {
                    doWelcome()
                }
            }
    }

    private fun doWelcome() {
        val intentHome = Intent(this, HomeActivity::class.java)
        intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val intentArticle = Intent(this, WebArticleActivity::class.java)
        intentArticle.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intentArticle.putExtra("articleId", WelActivity.tempId)

        startActivities(arrayOf(intentHome, intentArticle))
        finish()
    }

    override fun onAdFailed(p0: AdvanceError) {
        hasAdShow = true

//        doSecondUse()

        Log.e("OkHttp", p0.toString())
    }

    override fun onSdkSelected(id: String) {
        sdkId = id
    }

    override fun onAdClicked() {
        hasAdShow = true

        timerDelay(100) {
//        doSecondUse()
        }
    }

    override fun onAdSkip() {
        hasAdShow = true

        timerDelay(100) {
//        doSecondUse()
        }
    }

    override fun onAdTimeOver() {
        hasAdShow = true

        timerDelay(100) {
//        doSecondUse()
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