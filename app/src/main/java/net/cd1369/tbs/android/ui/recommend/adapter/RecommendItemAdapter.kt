package net.cd1369.tbs.android.ui.recommend.adapter

import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_recommend_item.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.RecommendItemEntity
import net.cd1369.tbs.android.util.V

class RecommendItemAdapter() :
    BaseQuickAdapter<RecommendItemEntity, BaseViewHolder>(R.layout.item_recommend_item) {
    override fun convert(helper: BaseViewHolder, item: RecommendItemEntity) {
        GlideApp.display(
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2Ftp01%2F1ZZQ20QJS6-0-lp.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642163406&t=0686e1efd8643055297537e52b5b11b4",
            helper.V.iv_img
        )
        helper.V.tv_name.text = "元宇宙${helper.layoutPosition}>>"
        helper.V.tv_desc.text = "描述"
    }
}
