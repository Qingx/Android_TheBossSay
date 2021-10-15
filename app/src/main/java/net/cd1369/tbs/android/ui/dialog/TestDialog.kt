package net.cd1369.tbs.android.ui.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import cn.wl.android.lib.ui.BaseActivity
import net.cd1369.tbs.android.R

/**
 * Created by Xiang on 2021/10/15 11:07
 * @description
 * @email Cymbidium@outlook.com
 */
class TestDialog : BaseActivity() {
    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, TestDialog::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.dialog_test
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
    }

    override fun finish() {
        super.finish()

    }
}