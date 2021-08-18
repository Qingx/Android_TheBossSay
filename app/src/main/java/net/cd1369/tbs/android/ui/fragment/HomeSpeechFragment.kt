package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.wl.android.lib.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_home_speech.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.ui.home.SearchArticleActivity
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class HomeSpeechFragment : BaseFragment() {
    val fragments = mutableListOf<Fragment>()

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
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        text_follow.isSelected = true
        text_follow.textSize = 24f

        view_pager.adapter = object : FragmentStateAdapter(mFragment) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    text_follow.isSelected = true
                    text_follow.textSize = 24f
                    text_square.isSelected = false
                    text_square.textSize = 16f
                } else {
                    text_square.isSelected = true
                    text_square.textSize = 24f
                    text_follow.isSelected = false
                    text_follow.textSize = 16f
                }
            }
        })

        view_pager.currentItem = 0
        view_pager.isUserInputEnabled = true

        text_follow doClick {
            view_pager.currentItem = 0
        }

        text_square doClick {
            view_pager.currentItem = 1
        }

        image_search doClick {
            SearchArticleActivity.start(mActivity)
        }
    }
}