package net.cd1369.tbs.android.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_home_tab.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.model.LabelModel
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/6/28 2:30 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class HomeTabAdapter :
    BaseQuickAdapter<LabelModel, BaseViewHolder>(R.layout.item_home_tab) {
    private var mSelectId: String = "-1"

    override fun convert(helper: BaseViewHolder, item: LabelModel) {
        helper.V.isSelected = item.id.toString() == mSelectId

        helper.V.text_content.text = item.name

        helper.V doClick {
            if (mSelectId != item.id.toString()) {
                val lastIndex = data.indexOfFirst {
                    it.id.toString() == mSelectId
                }

                mSelectId = item.id.toString()
                notifyItemChanged(lastIndex)
                notifyItemChanged(helper.layoutPosition)
                onSelect(item.id.toString())
            }
        }
    }

    abstract fun onSelect(select: String)
}