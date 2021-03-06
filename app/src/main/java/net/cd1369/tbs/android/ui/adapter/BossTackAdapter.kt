package net.cd1369.tbs.android.ui.adapter

import android.view.View
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_boss_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.model.BossSimpleModel
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl
import net.cd1369.tbs.android.util.getBossItemTime

/**
 * Created by Qing on 2021/6/30 12:43 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class BossTackAdapter :
    BaseQuickAdapter<BossSimpleModel, BaseViewHolder>(R.layout.item_boss_info) {
    override fun convert(helper: BaseViewHolder, item: BossSimpleModel) {
        helper.V.isSelected = item.isTop

        GlideApp.displayHead(item.head.fullUrl(), helper.V.image_head)

        item.showImage(
            helper.V.image_label1,
            helper.V.image_label2
        )

        helper.V.text_name.text = item.name
        helper.V.text_info.text = item.role
        helper.V.text_time.text = getBossItemTime(item.updateTime)

        helper.V.text_top.text = "取消置顶".takeIf { item.isTop } ?: "置顶"


        helper.V.cl_root doClick {
            onClick(item)
        }

        helper.V.text_top doClick {
            helper.V.sml_root.quickClose()

            val index = helper.layoutPosition + headerLayoutCount
            onDoTop(item, helper.V, index)
        }

        helper.V.text_delete doClick {
            helper.V.sml_root.quickClose()

            onCancelFollow(item)
        }
    }

    abstract fun onDoTop(item: BossSimpleModel, v: View, index: Int)

    abstract fun onCancelFollow(item: BossSimpleModel)
    abstract fun onClick(item: BossSimpleModel)

    fun notifyTopic(item: BossSimpleModel): Int {
        val rmIndex = mData.indexOf(item)
        val lastIndex = mData.lastIndex

        var taIndex = mData.indexOfFirst {
            it.checkSort(item)
        }

        if (taIndex < 0) taIndex = lastIndex
        val offset = if (taIndex > rmIndex) -1 else 0

        if (rmIndex == taIndex) return -1

        val entity = mData.removeAt(rmIndex)
        mData.add(taIndex + offset, entity)

        notifyItemMoved(rmIndex, taIndex + offset)
        return taIndex + offset
    }

}