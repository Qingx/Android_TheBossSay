package net.cd1369.tbs.android.ui.recommend.adapter

import androidx.core.view.isVisible
import cn.wl.android.lib.utils.DateFormat
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_print_sub.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.PrintSubEntity
import net.cd1369.tbs.android.ui.home.ArticleActivity
import net.cd1369.tbs.android.util.Tools.logE
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl

class PrintSubAdapter : BaseQuickAdapter<PrintSubEntity, BaseViewHolder>(R.layout.item_print_sub) {

    override fun convert(helper: BaseViewHolder, item: PrintSubEntity) {
        helper.V.tv_name.text = item.bossName
        helper.V.tv_desc.text = item.article?.descContent
        helper.V.tv_time.text = item.article?.let {
            DateFormat.date2YY_MM_dd_CN(it.releaseTime)
        } ?: ""

        GlideApp.display(item.head.fullUrl(), helper.V.iv_head)
        val filePath = item.article?.files?.getOrNull(0) ?: ""

        val ivPrintImg = helper.V.iv_print_img
        if (filePath.isNullOrEmpty()) {
            ivPrintImg.isVisible = false
        } else {
            ivPrintImg.isVisible = true
            val fullUrl = filePath.fullUrl().logE()
            GlideApp.display(fullUrl, ivPrintImg)
        }

        helper.V doClick {
            item.article?.id?.let { id ->
                ArticleActivity.start(it.context, id, false)
            }
        }
    }

}