package net.cd1369.tbs.android.ui.adapter

import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_grid_image.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.avatar

/**
 * Created by Qing on 2021/6/30 11:31 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class GridImageAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_grid_image) {
    override fun convert(helper: BaseViewHolder, item: String) {
        GlideApp.display(item.avatar(), helper.V.image_res)
    }
}