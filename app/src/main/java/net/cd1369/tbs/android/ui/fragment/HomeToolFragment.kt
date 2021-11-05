package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wl.android.lib.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_mine.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.ToolItem
import net.cd1369.tbs.android.ui.adapter.ToolItemAdapter
import net.cd1369.tbs.android.ui.home.WebDocActivity

/**
 * Created by Xiang on 2021/8/18 9:53
 * @description
 * @email Cymbidium@outlook.com
 */
class HomeToolFragment : BaseFragment() {
    private lateinit var mAdapter: ToolItemAdapter

    companion object {
        fun createFragment(): HomeToolFragment {
            return HomeToolFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_home_tool
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        mAdapter = object : ToolItemAdapter() {
            override fun onItemClick(item: ToolItem) {
                when (item) {
                    ToolItem.HuangLi -> WebDocActivity.start(
                        mActivity,
                        ToolItem.HuangLi.itemName,
                        "https://m.laohuangli.net/"
                    )
                    ToolItem.XingZuo -> WebDocActivity.start(
                        mActivity,
                        ToolItem.XingZuo.itemName,
                        "https://3g.d1xz.net/astro/"
                    )
                }
            }
        }

        rv_content.layoutManager = LinearLayoutManager(mActivity)
        rv_content.adapter = mAdapter

        val items = ToolItem.values().toMutableList()
        mAdapter.setNewData(items)
    }
}