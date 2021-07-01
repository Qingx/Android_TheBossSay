package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.wl.android.lib.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.doClick

class HomeActivity : BaseActivity() {
    val fragments = mutableListOf<Fragment>()

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, HomeActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_home
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        fragments.add(TalkFragment.createFragment())
        fragments.add(BossFragment.createFragment())
        fragments.add(MineFragment.createFragment())
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {

        view_pager.adapter = object : FragmentStateAdapter(mActivity) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                layout_talk.isSelected = position == 0
                layout_boss.isSelected = position == 1
                layout_mine.isSelected = position == 2
            }
        })

        view_pager.currentItem = 0
        view_pager.isUserInputEnabled = false

        layout_talk doClick {
            view_pager.setCurrentItem(0, false)
        }

        layout_boss doClick {
            view_pager.setCurrentItem(1, false)
        }

        layout_mine doClick {
            view_pager.setCurrentItem(2, false)
        }
    }
}