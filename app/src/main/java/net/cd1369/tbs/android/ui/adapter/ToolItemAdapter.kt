package net.cd1369.tbs.android.ui.adapter

import androidx.core.view.isVisible
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_mine.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.ToolItem
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/7/5 10:47 上午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class ToolItemAdapter : BaseQuickAdapter<ToolItem, BaseViewHolder>(R.layout.item_mine) {
    override fun convert(helper: BaseViewHolder, item: ToolItem) {
        helper.V.line_gray.isVisible = false
        helper.V.text_notice.isVisible = false

        helper.V.text_name.text = item.itemName
        GlideApp.display(item.icon, helper.V.image_icon)

        helper.V doClick {
            onItemClick(item)
        }
    }

    abstract fun onItemClick(item: ToolItem)

    fun onRefreshLogin() {
        notifyItemChanged(0 + headerLayoutCount)
    }
}