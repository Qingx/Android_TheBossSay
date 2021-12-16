package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.wl.android.lib.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_home_speech.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.ui.home.SearchArticleActivity
import net.cd1369.tbs.android.ui.recommend.HomeRecommendFrag
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class HomeSpeechFragment : BaseFragment() {

    private var mSelectView: TextView? = null

    private val tabViews = mutableListOf<View>()
    private val fragments = mutableListOf<Fragment>()

    companion object {
        fun createFragment(): HomeSpeechFragment {
            return HomeSpeechFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_home_speech
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        fragments.add(SpeechTackFragment.createFragment())
        fragments.add(SpeechSquareFragment.createFragment())
        fragments.add(HomeRecommendFrag.newIns())
        fragments.add(SpeechSquareFragment.createFragment())
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        tabViews.addAll(mutableListOf(
            text_follow,
            text_hot,
            text_recommend,
            text_square,
        ).onEachIndexed { index, tabView ->
            tabView.tag = index
            tabView.textSize = 18F

            tabView doClick {
                switchTabShow(it as TextView)

                val index = it.tag as Int
                view_pager.currentItem = index
            }
        })

        image_search doClick {
            SearchArticleActivity.start(mActivity)
        }

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                switchTabShow(tabViews[position] as TextView)
            }
        })
        view_pager.adapter = object : FragmentStateAdapter(mFragment) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

        view_pager.currentItem = 0
        view_pager.isUserInputEnabled = true
        view_pager.offscreenPageLimit = fragments.size

        switchTabShow(text_follow)
    }

    /**
     * 切换tab显示逻辑
     * @param tvSelect TextView
     */
    private fun switchTabShow(tvSelect: TextView) {
        val lastView = mSelectView
        if (tvSelect == lastView) {
            return
        }

        lastView?.textSize = 18F
        lastView?.isSelected = false
        lastView?.paint?.isFakeBoldText = false

        tvSelect.textSize = 22f
        tvSelect.isSelected = true
        tvSelect.paint.isFakeBoldText = true

        mSelectView = tvSelect
    }

}