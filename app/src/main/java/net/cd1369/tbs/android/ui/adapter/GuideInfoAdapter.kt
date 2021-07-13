package net.cd1369.tbs.android.ui.adapter

import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_guide_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.avatar
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/7/1 3:39 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class GuideInfoAdapter :
    BaseQuickAdapter<BossInfoEntity, BaseViewHolder>(R.layout.item_guide_info) {
    private val mSelect = HashSet<String>()

    override fun convert(helper: BaseViewHolder, item: BossInfoEntity) {
        helper.V.isSelected = mSelect.contains(item.id)

        GlideApp.display(item.head.avatar(), helper.V.image_head, R.drawable.ic_default_photo)
        helper.V.text_name.text = item.name
        helper.V.text_info.text = item.role


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

    override fun setNewData(data: MutableList<BossInfoEntity>?) {
        super.setNewData(data)

        autoSelectAll()
    }

    abstract fun onAddFollow(data: MutableList<String>)
}