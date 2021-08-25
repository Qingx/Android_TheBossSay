package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wl.android.lib.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_home_boss.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter
import net.cd1369.tbs.android.ui.home.SearchBossActivity
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Xiang on 2021/8/11 12:24
 * @description
 * @email Cymbidium@outlook.com
 */
class HomeBossFragment : BaseFragment() {
    private lateinit var tabAdapter: HomeTabAdapter
    private lateinit var mLabels: MutableList<BossLabelEntity>

    companion object {
        fun createFragment(): HomeBossFragment {
            return HomeBossFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_home_boss
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mLabels = DataConfig.get().bossLabels
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        text_title.paint.isFakeBoldText = true

        rv_tab.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically() = false
            }

        tabAdapter = object : HomeTabAdapter() {
            override fun onSelect(select: String) {
                val index = mLabels.indexOfFirst {
                    it.id == select
                }

                if (index != -1) {
                    view_pager.setCurrentItem(index, false)
                }
            }
        }

        rv_tab.adapter = tabAdapter
        tabAdapter.setNewData(mLabels)

        view_pager.adapter = object : FragmentStateAdapter(mFragment) {
            override fun getItemCount(): Int {
                return mLabels.size
            }

            override fun createFragment(position: Int): Fragment {
                return HomeBossContentFragment.createFragment(mLabels[position].id)
            }
        }

        view_pager.offscreenPageLimit = mLabels.size
        view_pager.isUserInputEnabled = false

        image_search doClick {
            SearchBossActivity.start(mActivity)
        }
    }
}