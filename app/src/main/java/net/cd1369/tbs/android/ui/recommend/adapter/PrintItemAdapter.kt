package net.cd1369.tbs.android.ui.recommend.adapter

import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_print_item.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.ArticlePrintEntity
import net.cd1369.tbs.android.ui.home.ArticleActivity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl

class PrintItemAdapter :
    BaseQuickAdapter<ArticlePrintEntity, BaseViewHolder>(R.layout.item_print_item) {
    override fun convert(helper: BaseViewHolder, item: ArticlePrintEntity) {
        helper.V.tv_item_desc.text = item.descContent ?: ""
        GlideApp.display(item.head.fullUrl(), helper.getView(R.id.iv_item_img))

        helper.V doClick {
            ArticleActivity.start(it.context, item.id, false)
        }
    }
}