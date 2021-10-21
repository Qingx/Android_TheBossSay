package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wl.android.lib.ui.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_mine_point.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.ui.fragment.MinePointFragment
import net.cd1369.tbs.android.util.doClick

class MinePointActivity : BaseActivity() {
    private val fragments = mutableListOf<MinePointFragment>()
    private val tabs = mutableListOf<String>()

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, MinePointActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_mine_point
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        fragments.add(MinePointFragment.createFragment("1"))
        fragments.add(MinePointFragment.createFragment("2"))
        tabs.add("文章")
        tabs.add("Boss语录")
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
        TabLayoutMediator(tab_view, view_pager) { tab, position ->
            tab.text = tabs[position]
        }.attach()

        view_pager.offscreenPageLimit = 2

        image_back doClick {
            onBackPressed()
        }
    }
}