package net.cd1369.tbs.android.ui.adapter

import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_boss_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/6/30 12:43 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class BossInfoAdapter : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_boss_info) {
    override fun convert(helper: BaseViewHolder, item: Int) {
        val hasIndex = helper.layoutPosition % 2 == 0
        GlideApp.display(R.drawable.ic_boss_label, helper.V.image_label)

        if (hasIndex) {
            GlideApp.display(R.drawable.ic_default_photo, helper.V.image_head)
            helper.V.text_name.text = "莉莉娅"
            helper.V.text_time.text = "最近1小时更新"
            helper.V.text_info.text = "灵魂莲华"
        } else {
            GlideApp.display(R.drawable.ic_test_head, helper.V.image_head)
            helper.V.text_name.text = "神里凌华"
            helper.V.text_time.text = "最近2小时更新"
            helper.V.text_info.text = "精神信仰"
        }

        helper.V doClick {
            onClick(item)
        }
    }

    abstract fun onClick(item: Int)
}