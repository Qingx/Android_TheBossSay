package net.cd1369.tbs.android.ui.test

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_speech_tack.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter

/**
 * Created by Xiang on 2021/8/11 10:00
 * @description
 * @email Cymbidium@outlook.com
 */
class SpeechTackFragment : BaseFragment() {
    private lateinit var tabAdapter: HomeTabAdapter

    companion object {
        fun createFragment(): SpeechTackFragment {
            return SpeechTackFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_speech_tack
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        rv_tab.layoutManager=object :LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL,false){
            override fun canScrollVertically()=false
        }

        tabAdapter=object :HomeTabAdapter(){
            override fun onSelect(select: String) {

            }
        }

        rv_tab.adapter=tabAdapter


    }

    override fun loadData() {
        super.loadData()

    }
}