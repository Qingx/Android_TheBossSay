package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.View
import cn.wl.android.lib.ui.BaseFragment
import net.cd1369.tbs.android.R

/**
 * Created by Xiang on 2021/10/21 17:02
 * @description
 * @email Cymbidium@outlook.com
 */
class MinePointFragment : BaseFragment() {
    private lateinit var type: String

    companion object {
        fun createFragment(type: String): MinePointFragment {
            return MinePointFragment().apply {
                arguments = Bundle().apply {
                    putString("type", type)
                }
            }
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_speech_tack_content
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        type = arguments?.getString("type") as String
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {

    }
}