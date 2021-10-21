package net.cd1369.tbs.android.ui.adapter

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_article_onlytext_withcontent.view.*
import kotlinx.android.synthetic.main.item_article_onlytext_withcontent.view.image_head
import kotlinx.android.synthetic.main.item_article_onlytext_withcontent.view.text_content
import kotlinx.android.synthetic.main.item_article_onlytext_withcontent.view.text_hot
import kotlinx.android.synthetic.main.item_article_onlytext_withcontent.view.text_name
import kotlinx.android.synthetic.main.item_article_onlytext_withcontent.view.text_time
import kotlinx.android.synthetic.main.item_article_singleimg_withcontent.view.*
import kotlinx.android.synthetic.main.item_article_singleimg_withcontent.view.text_info
import kotlinx.android.synthetic.main.item_article_singleimg_withcontent.view.text_title
import kotlinx.android.synthetic.main.item_article_singleimg_withcontent.view.view_type_msg
import kotlinx.android.synthetic.main.item_article_singleimg_withcontent.view.view_type_talk
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.model.ArticleSimpleModel
import net.cd1369.tbs.android.util.Tools.formatCount
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl
import net.cd1369.tbs.android.util.getArticleItemTime

/**
 * Created by Qing on 2021/6/28 5:17 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class ArticleTackAdapter :
    BaseMultiItemQuickAdapter<ArticleSimpleModel, BaseViewHolder>(mutableListOf()) {

    init {
        addItemType(0, R.layout.item_article_onlytext_withcontent)
        addItemType(1, R.layout.item_article_singleimg_withcontent)
    }

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: ArticleSimpleModel) {
        when (helper.itemViewType) {
            0 -> {
                helper.V.text_title.text = item.title
                GlideApp.displayHead(
                    item.bossHead.fullUrl(),
                    helper.V.image_head
                )
                helper.V.text_name.text = item.bossName
                helper.V.text_info.text = item.bossRole
                helper.V.text_content.text = item.descContent
                helper.V.text_hot.text =
                    "${item.collect!!.formatCount()}收藏·${item.readCount!!.formatCount()}人围观"
                helper.V.text_time.text = getArticleItemTime(item.showTime)

                helper.V.view_type_msg1.isVisible = item.isMsg
                helper.V.view_type_talk1.isVisible = item.isTalk
                helper.V.tv_only_update.isVisible = item.isLatestUpdate
            }
            1 -> {
                helper.V.text_title.text = item.title
                GlideApp.displayHead(
                    item.bossHead.fullUrl(),
                    helper.V.image_head
                )
                helper.V.text_name.text = item.bossName
                helper.V.text_info.text = item.bossRole
                GlideApp.displayHead(item.files!!.getOrNull(0)!!.fullUrl(), helper.V.image_res)
                helper.V.text_content.text = item.descContent
                helper.V.text_hot.text =
                    "${item.collect!!.formatCount()}收藏·${item.readCount!!.formatCount()}人围观"
                helper.V.text_time.text = getArticleItemTime(item.showTime)

                // 判断言论
                helper.V.view_type_msg.isVisible = item.isMsg
                helper.V.view_type_talk.isVisible = item.isTalk
                helper.V.tv_single_update.isVisible = item.isLatestUpdate
            }
        }

        helper.V doClick {
            onClick(item)

            if (!item.isRead) {
                item.isRead = true
                notifyItemChanged(helper.layoutPosition)
            }
        }
    }

    abstract fun onClick(item: ArticleSimpleModel)
}