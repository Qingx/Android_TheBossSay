package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Toasts
import kotlinx.android.synthetic.main.activity_user_info.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.ui.adapter.UserInfoAdapter
import net.cd1369.tbs.android.util.doClick

class UserInfoActivity : BaseActivity() {
    private lateinit var mAdapter: UserInfoAdapter

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, UserInfoActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_user_info
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        image_back doClick {
            onBackPressed()
        }

        mAdapter = object : UserInfoAdapter() {
            override fun onItemClick(item: Int) {
                Toasts.show("修改")
            }
        }

        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        rv_content.adapter = mAdapter
        mAdapter.setNewData(mutableListOf(0, 1, 2))
    }
}