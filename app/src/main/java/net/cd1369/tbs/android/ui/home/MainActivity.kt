package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.wl.android.lib.ui.BaseActivity
import net.cd1369.tbs.android.R

class MainActivity : BaseActivity() {
    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, MainActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_main
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {

    }
}