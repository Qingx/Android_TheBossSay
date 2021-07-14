package net.cd1369.tbs.android.ui.adapter

import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_search_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.avatar
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/6/30 5:01 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class SearchInfoAdapter :
    BaseQuickAdapter<BossInfoEntity, BaseViewHolder>(R.layout.item_search_info) {
    override fun convert(helper: BaseViewHolder, item: BossInfoEntity) {

        GlideApp.display(item.head.avatar(), helper.V.image_head, R.drawable.ic_default_photo)
        helper.V.text_name.text = item.name
        helper.V.text_info.text = item.role
        helper.V.layout_follow.isSelected = !item.isCollect

        helper.V.layout_follow doClick {
            onClickFollow(item)
        }
    }

    abstract fun onClickFollow(item: BossInfoEntity)
}