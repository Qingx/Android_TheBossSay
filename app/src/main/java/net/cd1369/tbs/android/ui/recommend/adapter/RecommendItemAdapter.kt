package net.cd1369.tbs.android.ui.recommend.adapter

import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_recommend_item.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.RecommendItemEntity
import net.cd1369.tbs.android.ui.recommend.PrintActivity
import net.cd1369.tbs.android.ui.recommend.PrintSubActivity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl

class RecommendItemAdapter() :
    BaseQuickAdapter<RecommendItemEntity, BaseViewHolder>(R.layout.item_recommend_item) {
    override fun convert(helper: BaseViewHolder, item: RecommendItemEntity) {
        GlideApp.display(
            item.background.fullUrl(),
            helper.V.iv_img
        )
        helper.V.tv_name.text = item.subjectName + ">>"
        helper.V.tv_desc.text = item.introduction

        helper.V doClick {
            PrintSubActivity.start(it.context, item.id)
        }
    }
}
