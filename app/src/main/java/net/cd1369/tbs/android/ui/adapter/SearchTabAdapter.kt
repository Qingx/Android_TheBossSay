package net.cd1369.tbs.android.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_search_tab.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/6/30 4:25 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class SearchTabAdapter :
    BaseQuickAdapter<BossLabelEntity, BaseViewHolder>(R.layout.item_search_tab) {
    private var mSelectId = "-1"
    override fun convert(helper: BaseViewHolder, item: BossLabelEntity) {
        helper.V.isSelected = mSelectId == item.id


        helper.V.text_content.text = if (item.id == "-1") {
            "为你推荐"
        } else item.name

        helper.V doClick {
            if (mSelectId != item.id) {
                val lastIndex = data.indexOfFirst {
                    it.id == mSelectId
                }
                mSelectId = item.id

                notifyItemChanged(lastIndex)
                notifyItemChanged(helper.layoutPosition)

                onClick(item.id)
            }
        }
    }

    abstract fun onClick(item: String)
}