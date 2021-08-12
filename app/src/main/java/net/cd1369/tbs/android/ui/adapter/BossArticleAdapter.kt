package net.cd1369.tbs.android.ui.adapter

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_article_onlytext_withcontent_boss.view.image_head
import kotlinx.android.synthetic.main.item_article_onlytext_withcontent_boss.view.text_content
import kotlinx.android.synthetic.main.item_article_onlytext_withcontent_boss.view.text_hot
import kotlinx.android.synthetic.main.item_article_onlytext_withcontent_boss.view.text_name
import kotlinx.android.synthetic.main.item_article_onlytext_withcontent_boss.view.text_time
import kotlinx.android.synthetic.main.item_article_singleimg_withcontent_boss.view.*
import kotlinx.android.synthetic.main.item_article_singleimg_withcontent_boss.view.layout_content
import kotlinx.android.synthetic.main.item_article_singleimg_withcontent_boss.view.text_info
import kotlinx.android.synthetic.main.item_article_singleimg_withcontent_boss.view.text_title
import kotlinx.android.synthetic.main.item_article_singleimg_withcontent_boss.view.text_update
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.util.*
import net.cd1369.tbs.android.util.Tools.formatCount

/**
 * Created by Qing on 2021/6/28 5:17 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class BossArticleAdapter :
    BaseMultiItemQuickAdapter<ArticleEntity, BaseViewHolder>(mutableListOf()) {

    init {
        addItemType(0, R.layout.item_article_onlytext_withcontent_boss)
        addItemType(1, R.layout.item_article_singleimg_withcontent_boss)
    }

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: ArticleEntity) {
        when (helper.itemViewType) {
            0 -> {
                helper.V.layout_content.isSelected = item.isRead
                helper.V.text_update.isVisible =
                    !item.isRead && Tools.showRedDots(item.bossId, item.getTime())

                helper.V.text_title.text = item.title
                GlideApp.displayHead(
                    item.bossVO.head.avatar(),
                    helper.V.image_head
                )
                helper.V.text_name.text = item.bossVO.name
                helper.V.text_info.text = item.bossVO.role
                helper.V.text_content.text = item.descContent
                helper.V.text_hot.text =
                    "${item.collect!!.formatCount()}收藏·${item.readCount!!.formatCount()}人围观"
                helper.V.text_time.text = getArticleItemTime(item.getTime())
            }
            1 -> {
                helper.V.layout_content.isSelected = item.isRead
                helper.V.text_update.isVisible =
                    !item.isRead && Tools.showRedDots(item.bossId, item.getTime())

                helper.V.text_title.text = item.title
                GlideApp.displayHead(
                    item.bossVO.head.avatar(),
                    helper.V.image_head
                )
                helper.V.text_name.text = item.bossVO.name
                helper.V.text_info.text = item.bossVO.role
                GlideApp.displayHead(item.files!!.getOrNull(0)!!.avatar(), helper.V.image_res)
                helper.V.text_content.text = item.descContent
                helper.V.text_hot.text =
                    "${item.collect!!.formatCount()}收藏·${item.readCount!!.formatCount()}人围观"
                helper.V.text_time.text = getArticleItemTime(item.getTime())
            }
        }

        helper.V doClick {
            onClick(item)
        }
    }

    abstract fun onClick(item: ArticleEntity)
}