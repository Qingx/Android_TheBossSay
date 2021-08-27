package net.cd1369.tbs.android.ui.adapter

import android.view.View
import androidx.core.view.isVisible
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_search_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.BossLabelItem
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl

/**
 * Created by Qing on 2021/6/30 5:01 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class SearchInfoAdapter :
    BaseQuickAdapter<BossInfoEntity, BaseViewHolder>(R.layout.item_search_info) {

    val mIdSet = hashSetOf<String>()
    var isEdit = false
        set(value) {
            field = value
            mIdSet.clear()
            notifyDataSetChanged()
        }

    override fun convert(helper: BaseViewHolder, item: BossInfoEntity) {
        GlideApp.displayHead(item.head.fullUrl(), helper.V.image_head)
        helper.V.text_name.text = item.name
        helper.V.text_info.text = item.role
        helper.V.layout_follow.isSelected = !item.isCollect

        helper.V.ll_info_select.isVisible = isEdit && !item.isCollect
        helper.V.ll_info_select.isSelected = item.id in mIdSet
        helper.V.ll_info_select doClick {
            selectItem(it, item)
        }

        helper.V.image_type.isVisible =
            BossLabelItem.Empty != BossLabelItem.get(item.bossType)
        if (item.bossType != BossLabelItem.Empty.code) {
            GlideApp.display(BossLabelItem.get(item.bossType).icon, helper.V.image_type)
        }

        helper.V.layout_follow doClick {
            if (isEdit) {
                selectItem(helper.V.ll_info_select, item)
            } else {
                onClickFollow(item)
            }
        }

        helper.V doClick {
            if (isEdit) {
                selectItem(helper.V.ll_info_select, item)
            } else {
                onItemClick(item)
            }
        }
    }

    private fun selectItem(it: View, item: BossInfoEntity) {
        it.isSelected = !it.isSelected

        if (it.isSelected) {
            mIdSet.add(item.id)
        } else {
            mIdSet.remove(item.id)
        }
    }

    override fun setNewData(data: List<BossInfoEntity>?) {
        mIdSet.clear()

        super.setNewData(data)
    }

    abstract fun onItemClick(item: BossInfoEntity)

    abstract fun onClickFollow(item: BossInfoEntity)

    fun doFollowChange(id: String, status: Boolean) {
        val position = mData.indexOfFirst {
            id == it.id
        }

        if (position != -1) {
            mData[position].isCollect = status

            notifyItemChanged(position)
        }
    }
}