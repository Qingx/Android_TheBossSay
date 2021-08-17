package net.cd1369.tbs.android.ui.start

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import cn.wl.android.lib.ui.BaseActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import java.util.concurrent.TimeUnit

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 11:56 2021/8/6
 * @desc 网页启动界面
 */
class WelActivity : Activity() {

    private var mDis: Disposable? = null

    companion object {
        var tempId: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tempId = intent?.data?.getQueryParameter("id") ?: ""
        setContentView(R.layout.activity_start)

        mDis = Observable.timer(100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                SplashActivity.start(this)
                finish()
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        mDis?.dispose()
    }

}