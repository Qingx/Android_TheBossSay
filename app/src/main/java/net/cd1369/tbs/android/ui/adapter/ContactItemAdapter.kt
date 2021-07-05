package net.cd1369.tbs.android.ui.adapter

import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_contact.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.ContactItem
import net.cd1369.tbs.android.util.V

/**
 * Created by Qing on 2021/7/5 7:05 下午
 * @description
 * @email Cymbidium@outlook.com
 */
class ContactItemAdapter : BaseQuickAdapter<ContactItem, BaseViewHolder>(R.layout.item_contact) {
    override fun convert(helper: BaseViewHolder, item: ContactItem) {
        helper.V.text_name.text = item.itemName

        GlideApp.display(item.icon, helper.V.image_icon)
    }
}