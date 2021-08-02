package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.GlideApp
import kotlinx.android.synthetic.main.activity_boss_info.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.util.avatar
import net.cd1369.tbs.android.util.doClick

class BossInfoActivity : BaseActivity() {
    private lateinit var entity: BossInfoEntity

    companion object {
        fun start(context: Context?, entity: BossInfoEntity) {
            val intent = Intent(context, BossInfoActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtras(Bundle().apply {
                        putSerializable("entity", entity)
                    })
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_boss_info
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        entity = intent.getSerializableExtra("entity") as BossInfoEntity
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        GlideApp.displayHead(entity.head.avatar(), image_head)
        text_name.text = entity.name
        text_info.text = entity.role
        text_content.text = entity.info

        image_back doClick {
            onBackPressed()
        }
    }
}