package net.cd1369.tbs.android.ui.adapter

import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_guide_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/7/1 3:39 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class GuideInfoAdapter : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_guide_info) {
    private val mSelect = mutableListOf<Int>()
    override fun convert(helper: BaseViewHolder, item: Int) {
        helper.V.isSelected = mSelect.contains(item)

        val hasIndex = helper.layoutPosition % 2 == 0

        if (hasIndex) {
            GlideApp.display(R.drawable.ic_default_photo, helper.V.image_head)
            helper.V.text_name.text = "莉莉娅"
            helper.V.text_info.text = "灵魂莲华"
        } else {
            GlideApp.display(R.drawable.ic_test_head, helper.V.image_head)
            helper.V.text_name.text = "神里凌华"
            helper.V.text_info.text = "精神信仰"
        }

        helper.V doClick {
            if (mSelect.contains(item)) {
                mSelect.remove(item)
            } else {
                mSelect.add(item)
            }

            notifyItemChanged(helper.layoutPosition)
        }
    }

    fun addAll() {
        mSelect.clear()
        mSelect.addAll(data)

        notifyDataSetChanged()

        onAddAll(data)
    }

    fun clearAll() {
        mSelect.clear()

        notifyDataSetChanged()
    }

    abstract fun onAddAll(data: MutableList<Int>)
}