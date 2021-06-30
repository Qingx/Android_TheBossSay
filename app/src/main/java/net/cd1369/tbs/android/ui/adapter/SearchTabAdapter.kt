package net.cd1369.tbs.android.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_search_tab.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/6/30 4:25 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class SearchTabAdapter : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_search_tab) {
    private var mSelectIndex = 0
    override fun convert(helper: BaseViewHolder, item: Int) {
        helper.V.isSelected = mSelectIndex == helper.layoutPosition

        val hasIndex = helper.layoutPosition % 2 == 0

        helper.V.text_content.text = if (helper.layoutPosition == 0) {
            "为你推荐"
        } else {
            if (hasIndex) "混子上单" else "草食打野"
        }

        helper.V doClick {
            val currentIndex = helper.layoutPosition
            if (currentIndex != mSelectIndex) {
                val lastIndex = mSelectIndex
                mSelectIndex = currentIndex

                notifyItemChanged(mSelectIndex)
                notifyItemChanged(lastIndex)

                onClick(item)
            }
        }
    }

    abstract fun onClick(item: Int)
}