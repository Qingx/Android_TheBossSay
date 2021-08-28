package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.wl.android.lib.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_speech_tack.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.db.LabelDaoManager
import net.cd1369.tbs.android.data.model.LabelModel
import net.cd1369.tbs.android.event.GlobalScrollEvent
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter
import net.cd1369.tbs.android.util.Tools.isLabelsEmpty

/**
 * Created by Xiang on 2021/8/11 12:02
 * @description
 * @email Cymbidium@outlook.com
 */
class SpeechSquareFragment : BaseFragment() {
    private lateinit var tabAdapter: HomeTabAdapter
    private lateinit var mLabels: MutableList<LabelModel>

    companion object {
        fun createFragment(): SpeechSquareFragment {
            return SpeechSquareFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_speech_tack
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        rv_tab.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically() = false
            }

        tabAdapter = object : HomeTabAdapter() {
            override fun onSelect(select: String) {
                val index = mLabels.indexOfFirst {
                    it.id.toString() == select
                }

                if (index != -1) {
                    view_pager.setCurrentItem(index, false)
                }
            }
        }

        rv_tab.adapter = tabAdapter

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                GlobalScrollEvent.squareLabel = mLabels[position].id.toString()
            }
        })

        view_pager.isUserInputEnabled = false
    }

    override fun loadData() {
        super.loadData()

        mLabels = LabelDaoManager.getInstance(mActivity).findAll()

        if (!mLabels.isLabelsEmpty()) {
            tabAdapter.setNewData(mLabels)

            view_pager.adapter = object : FragmentStateAdapter(mFragment) {
                override fun getItemCount(): Int {
                    return mLabels.size
                }

                override fun createFragment(position: Int): Fragment {
                    return SpeechSquareContentFragment.createFragment(mLabels[position].id)
                }
            }

            view_pager.offscreenPageLimit = mLabels.size
        } else {
            TbsApi.boss().obtainBossLabels().onErrorReturn { mutableListOf() }.bindSubscribe {
                it.add(0, LabelModel.empty)
                LabelDaoManager.getInstance(mActivity).insertList(it)
                mLabels = it

                tabAdapter.setNewData(mLabels)

                view_pager.adapter = object : FragmentStateAdapter(mFragment) {
                    override fun getItemCount(): Int {
                        return mLabels.size
                    }

                    override fun createFragment(position: Int): Fragment {
                        return SpeechSquareContentFragment.createFragment(mLabels[position].id)
                    }
                }

                view_pager.offscreenPageLimit = mLabels.size
            }
        }
    }
}