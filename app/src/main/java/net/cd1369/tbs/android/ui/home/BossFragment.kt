package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.View
import cn.wl.android.lib.ui.BaseFragment
import net.cd1369.tbs.android.R

/**
 * Created by QingHe on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class BossFragment :BaseFragment(){
    companion object{
        fun createFragment(): BossFragment {
            return BossFragment()
        }
    }
    override fun getLayoutResource(): Any {
        return R.layout.fragment_boss
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
    }
}