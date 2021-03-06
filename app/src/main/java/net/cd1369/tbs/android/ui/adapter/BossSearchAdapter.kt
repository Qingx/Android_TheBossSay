package net.cd1369.tbs.android.ui.adapter

import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_boss_search_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl
import net.cd1369.tbs.android.util.getBossItemTime

/**
 * Created by Qing on 2021/6/30 12:43 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class BossSearchAdapter :
    BaseQuickAdapter<BossInfoEntity, BaseViewHolder>(R.layout.item_boss_search_info) {
    override fun convert(helper: BaseViewHolder, item: BossInfoEntity) {

        GlideApp.displayHead(item.head.fullUrl(), helper.V.image_head)
        GlideApp.display(R.drawable.ic_boss_label, helper.V.image_label)
        helper.V.text_name.text = item.name
        helper.V.text_info.text = item.role
        helper.V.text_time.text = getBossItemTime(item.updateTime)

        helper.V doClick {
            onClick(item)
        }
    }

    abstract fun onClick(item: BossInfoEntity)
}