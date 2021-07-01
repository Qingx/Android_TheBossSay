package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import cn.wl.android.lib.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_boss_info.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.doClick

class BossInfoActivity : BaseActivity() {
    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, BossInfoActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_boss_info
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        image_back doClick {
            onBackPressed()
        }
    }
}