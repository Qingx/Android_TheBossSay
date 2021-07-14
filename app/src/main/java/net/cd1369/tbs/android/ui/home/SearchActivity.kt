package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wl.android.lib.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_search.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.event.SearchCancelEvent
import net.cd1369.tbs.android.event.SearchEvent
import net.cd1369.tbs.android.util.doClick

class SearchActivity : BaseActivity() {
    val fragments = mutableListOf<Fragment>()
    val resultFragment = SearchResultFragment.createFragment()

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, SearchActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_search
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        fragments.add(SearchFragment.createFragment())
        fragments.add(resultFragment)
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {

        edit_input.hint = "大家都在搜莉莉娅"


        view_pager.adapter = object : FragmentStateAdapter(mActivity) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

        view_pager.isUserInputEnabled = false

        edit_input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && !edit_input.text.toString()
                    .isNullOrEmpty()
            ) {
                view_pager.setCurrentItem(1, false)
                resultFragment.eventSearch(edit_input.text.toString())
                eventBus.post(SearchEvent(edit_input.text.toString()))
            }
            false
        }

        image_back doClick {
            onBackPressed()
        }

        image_cancel doClick {
            if (!edit_input.text.toString().isNullOrEmpty()) {
                eventBus.post(SearchCancelEvent())
                view_pager.setCurrentItem(0, false)
                edit_input.setText("")
            }
        }
    }
}