package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.utils.Toasts
import kotlinx.android.synthetic.main.fragment_mine.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.MineItem
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.ui.adapter.MineItemAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class MineFragment : BaseFragment() {
    private lateinit var mAdapter: MineItemAdapter

    companion object {
        fun createFragment(): MineFragment {
            return MineFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_mine
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        mAdapter = object : MineItemAdapter() {
            override fun onItemClick(item: MineItem) {
                Toasts.show(item.itemName)
            }
        }

        if (UserConfig.get().loginStatus) {
            text_name.text = "清和"
            text_id.text = "ID：101010101"
        } else {
            text_name.text = "请先登录！"
            text_id.text = "游客：123456789"
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
        mAdapter.setNewData(MineItem.values().toMutableList())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: LoginEvent) {
        mAdapter.onRefreshLogin()

        if (event.status) {
            text_name.text = "请先登录！"
            text_id.text = "游客：123456789"
        } else {
            text_name.text = "清和"
            text_id.text = "ID：101010101"
        }
    }
}