package net.cd1369.tbs.android.ui.adapter

import android.view.View
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_boss_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.util.*
import java.util.*

/**
 * Created by Qing on 2021/6/30 12:43 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class BossInfoAdapter :
    BaseQuickAdapter<BossInfoEntity, BaseViewHolder>(R.layout.item_boss_info) {
    override fun convert(helper: BaseViewHolder, item: BossInfoEntity) {
        GlideApp.displayHead(item.head.avatar(), helper.V.image_head)

        item.showImage(
            helper.V.image_label1,
            helper.V.image_label2)

        helper.V.text_name.text = item.name
        helper.V.text_info.text = item.role
        helper.V.text_time.text = getBossItemTime(item.updateTime)

        helper.V.cl_root doClick {
            onClick(item)
        }

        helper.V.text_top.text = "取消置顶".takeIf { item.top } ?: "置顶"
        helper.V.text_top doClick {
            helper.V.sml_root.quickClose()

            var index = helper.layoutPosition + headerLayoutCount
            onDoTop(item, helper.V, index)
        }

        helper.V.text_delete doClick {
            helper.V.sml_root.quickClose()

            onCancelFollow(item)
        }

        helper.V.isSelected = item.isTop
    }

    abstract fun onDoTop(item: BossInfoEntity, v: View, index: Int)

    abstract fun onCancelFollow(item: BossInfoEntity)
    abstract fun onClick(item: BossInfoEntity)

    fun notifyTopic(item: BossInfoEntity, fp: Int, lp: Int): Int {
        var rmIndex = mData.indexOf(item)
        var lastIndex = mData.lastIndex

        var taIndex = mData.indexOfFirst {
            it.checkSort(item)
        }

        if (taIndex < 0) taIndex = lastIndex
        var offset = if (taIndex > rmIndex) -1 else 0

        if (rmIndex == taIndex) return -1

        var item = mData.removeAt(rmIndex)
        mData.add(taIndex + offset, item)

        notifyItemMoved(rmIndex, taIndex + offset)
        return taIndex + offset
    }

}