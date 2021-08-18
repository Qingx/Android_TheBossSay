package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.View
import cn.wl.android.lib.ui.BaseFragment
import net.cd1369.tbs.android.R

/**
 * Created by Xiang on 2021/8/18 9:53
 * @description
 * @email Cymbidium@outlook.com
 */
class HomeToolFragment : BaseFragment() {
    companion object {
        fun createFragment(): HomeToolFragment {
            return HomeToolFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_home_tool
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
    }
}