package net.cd1369.tbs.android.ui.adapter

import androidx.core.view.isVisible
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_follow_card.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.model.BossSimpleModel
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.fullUrl
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/6/28 3:50 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class FollowCardAdapter :
    BaseQuickAdapter<BossSimpleModel, BaseViewHolder>(R.layout.item_follow_card) {
    override fun convert(helper: BaseViewHolder, item: BossSimpleModel) {
        GlideApp.displayHead(item.head.fullUrl(), helper.V.image_head)

        helper.V.text_name.text = item.name
        helper.V.text_info.text = item.role

        helper.V.view_red_dots.isVisible = Tools.showRedDots(item.id.toString(), item.updateTime)

        helper.V doClick {
            onClick(item)
        }
    }

    abstract fun onClick(item: BossSimpleModel)
}