package net.cd1369.tbs.android.ui.test

import android.os.Bundle
import android.view.View
import cn.wl.android.lib.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_speech_tack_content_fragment.*
import net.cd1369.tbs.android.R

/**
 * Created by Xiang on 2021/8/11 10:05
 * @description
 * @email Cymbidium@outlook.com
 */
class SpeechTackContentFragment : BaseFragment() {
    private lateinit var mLabel: String

    companion object {
        fun createFragment(label: String): SpeechTackContentFragment {
            return SpeechTackContentFragment().apply {
                arguments = Bundle().apply {
                    putString("label", label)
                }
            }
        }
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mLabel = arguments?.getString("label") as String
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_speech_tack_content_fragment
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        text_content.text = mLabel
    }
}