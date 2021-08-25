package net.cd1369.tbs.android.ui.start

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.cd1369.tbs.android.R

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 15:29 2021/8/25
 * @desc 启动界面
 */
class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        SplashActivity.start(this)
        finish()
    }

}