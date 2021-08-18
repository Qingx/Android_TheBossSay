package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.utils.Toasts
import kotlinx.android.synthetic.main.fragment_mine.*
import net.cd1369.tbs.android.BuildConfig
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.MineItem
import net.cd1369.tbs.android.ui.adapter.MineItemAdapter
import net.cd1369.tbs.android.ui.home.MineAboutAppActivity
import net.cd1369.tbs.android.ui.home.MineContactAuthorActivity
import net.cd1369.tbs.android.ui.home.WebActivity

/**
 * Created by Xiang on 2021/8/18 9:53
 * @description
 * @email Cymbidium@outlook.com
 */
class HomeToolFragment : BaseFragment() {
    private lateinit var mAdapter: MineItemAdapter

    companion object {
        fun createFragment(): HomeToolFragment {
            return HomeToolFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_home_tool
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        mAdapter = object : MineItemAdapter() {
            override fun onItemClick(item: MineItem) {
                when (item) {
                    MineItem.HuangLi -> WebActivity.start(
                        mActivity,
                        MineItem.HuangLi.itemName,
                        "https://m.laohuangli.net/"
                    )
                    MineItem.XingZuo -> WebActivity.start(
                        mActivity,
                        MineItem.XingZuo.itemName,
                        "https://3g.d1xz.net/astro/"
                    )
                    else -> Toasts.show(item.itemName)
                }
            }
        }

        rv_content.layoutManager = LinearLayoutManager(mActivity)
        rv_content.adapter = mAdapter

        val items = MineItem.values().toMutableList()
        items.remove(MineItem.Favorite)
        items.remove(MineItem.History)
        items.remove(MineItem.Share)
        items.remove(MineItem.About)
        items.remove(MineItem.Contact)

        mAdapter.setNewData(items)
    }
}