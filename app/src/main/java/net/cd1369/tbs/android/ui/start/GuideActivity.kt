package net.cd1369.tbs.android.ui.start

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Toasts
import kotlinx.android.synthetic.main.activity_guide.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.ui.adapter.GuideInfoAdapter
import net.cd1369.tbs.android.ui.home.HomeActivity
import net.cd1369.tbs.android.util.doClick

class GuideActivity : BaseActivity() {
    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, GuideActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_guide
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {

        countdown(5) {
            text_time.text = "${it}s"

            if (it <= 0) {
                text_time.text = "跳过"
            }
        }

        val adapter = object : GuideInfoAdapter() {
            override fun onAddAll(data: MutableList<Int>) {
                Toasts.show(data.toString())
                DataConfig.get().firstUse = true
                HomeActivity.start(mActivity)
                mActivity?.finish()
            }
        }

        rv_content.adapter = adapter
        rv_content.layoutManager =
            object : GridLayoutManager(mActivity, 2, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        adapter.setNewData(mutableListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13))

        layout_add doClick {
            adapter.addAll()
        }

        text_clear doClick {
            adapter.clearAll()
        }

        text_time doClick {
            DataConfig.get().firstUse = true
            HomeActivity.start(mActivity)
            mActivity?.finish()
        }
    }
}