package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_contact.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.ContactItem
import net.cd1369.tbs.android.ui.adapter.ContactItemAdapter
import net.cd1369.tbs.android.util.doClick

class ContactActivity : BaseActivity() {
    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, ContactActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_contact
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        text_mail.text = "xxxxxxx@.com"

        val adapter = ContactItemAdapter()
        rv_content.layoutManager =
            object : GridLayoutManager(mActivity, 2, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        rv_content.adapter = adapter
        adapter.setNewData(ContactItem.values().toMutableList())

        image_back doClick {
            onBackPressed()
        }
    }
}