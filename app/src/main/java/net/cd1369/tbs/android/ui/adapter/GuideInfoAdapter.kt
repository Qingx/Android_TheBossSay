package net.cd1369.tbs.android.ui.adapter

import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_guide_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.model.BossSimpleModel
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl

/**
 * Created by Qing on 2021/7/1 3:39 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class GuideInfoAdapter :
    BaseQuickAdapter<BossSimpleModel, BaseViewHolder>(R.layout.item_guide_info) {
    private val mSelect = HashSet<String>()

    override fun convert(helper: BaseViewHolder, item: BossSimpleModel) {
        helper.V.isSelected = mSelect.contains(item.id)

        GlideApp.displayHead(item.head.fullUrl(), helper.V.image_head)
        helper.V.text_name.text = item.name
        helper.V.text_info.text = item.role
        if (!item.photoUrl.isNullOrEmpty()) {
            GlideApp.display(item.photoUrl[0].fullUrl(), helper.V.image_label)
        }

        helper.V.text_name.paint.isFakeBoldText = true

        helper.V doClick {
            if (mSelect.contains(item.id)) {
                mSelect.remove(item.id)
            } else {
                mSelect.add(item.id)
            }

            notifyItemChanged(helper.layoutPosition)
        }
    }

    fun addFollow() {
        if (mSelect.isNullOrEmpty()) {
            Toasts.show("请至少选择一位老板")
        } else {
            onAddFollow(mSelect.toMutableList())
        }
    }

    fun clearAll() {
        mSelect.clear()

        notifyDataSetChanged()
    }

    private fun autoSelectAll() {
        mSelect.addAll(mData.map { it.id })
        notifyDataSetChanged()
    }

    override fun setNewData(data: MutableList<BossSimpleModel>?) {
        super.setNewData(data)

        autoSelectAll()
    }

    abstract fun onAddFollow(data: MutableList<String>)
}