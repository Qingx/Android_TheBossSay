package net.cd1369.tbs.android.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_home_tab.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/6/28 2:30 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class HomeTabAdapter :
    BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_home_tab) {
    var mSelectId: Int? = null

    override fun convert(helper: BaseViewHolder, item: Int) {
        helper.V.isSelected = item == mSelectId ?: data[0]

        val hasIndex = helper.layoutPosition % 2 == 0
        helper.V.text_content.text = if (hasIndex) "神里凌华" else "莉莉娅"

        helper.V doClick {
            if (mSelectId != item) {
                val lastIndex = data.indexOfFirst {
                    it == mSelectId
                }

                mSelectId = item
                notifyItemChanged(lastIndex)
                notifyItemChanged(helper.layoutPosition)
                onSelect(item)
            }
        }
    }

    abstract fun onSelect(select: Int)
}