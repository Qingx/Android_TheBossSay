package net.cd1369.tbs.android.ui.adapter

import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_grid_image.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.V

/**
 * Created by Qing on 2021/6/30 11:31 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class GridImageAdapter : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_grid_image) {
    override fun convert(helper: BaseViewHolder, item: Int) {
        GlideApp.display(
            if (helper.layoutPosition % 2 == 0) R.drawable.ic_default_photo else R.drawable.ic_test_head,
            helper.V.image_res
        )
    }
}