package net.cd1369.tbs.android.ui.adapter

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import cn.wl.android.lib.utils.DateFormat
import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_folder_article.view.*
import kotlinx.android.synthetic.main.item_folder_daily.view.image_head
import kotlinx.android.synthetic.main.item_folder_daily.view.layout_content
import kotlinx.android.synthetic.main.item_folder_daily.view.text_delete
import kotlinx.android.synthetic.main.item_folder_daily.view.text_name
import kotlinx.android.synthetic.main.item_folder_daily.view.text_time
import kotlinx.android.synthetic.main.item_folder_daily.view.text_title
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.HisFavEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl

/**
 * Created by Xiang on 2021/10/26 15:09
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class FolderContentAdapter :
    BaseMultiItemQuickAdapter<HisFavEntity, BaseViewHolder>(mutableListOf()) {
    init {
        addItemType(1, R.layout.item_folder_article)
        addItemType(2, R.layout.item_folder_daily)
    }

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: HisFavEntity) {
        when (helper.itemViewType) {
            1 -> {
                helper.V.layout_include.isVisible = !item.hidden
                helper.V.layout_content.isSelected = item.hidden
                helper.V.text_time.isVisible = !item.hidden

                if (item.hidden) {
                    GlideApp.display(
                        R.drawable.ic_article_cover,
                        helper.V.image_cover,
                        R.drawable.ic_article_cover
                    )
                    helper.V.text_title.text = "Boss已下架，内容不予显示"
                    helper.V.text_name.text = item.bossName
                    GlideApp.displayHead(item.bossHead.fullUrl(), helper.V.image_head)
                } else {
                    GlideApp.display(
                        item.cover.fullUrl(),
                        helper.V.image_cover,
                        R.drawable.ic_article_cover
                    )
                    helper.V.text_title.text = item.content
                    GlideApp.displayHead(item.bossHead.fullUrl(), helper.V.image_head)
                    helper.V.text_name.text = item.bossName
                    helper.V.text_time.text = "${DateFormat.date2yymmdd(item.createTime)}收藏"
                    helper.V.view_only_talk.isVisible = item.articleType == "1"
                    helper.V.view_only_msg.isVisible = item.articleType == "2"
                }

                helper.V.layout_content doClick {
                    if (!item.hidden) {
                        onArticleClick(item)
                    } else {
                        Toasts.show("Boss已下架，内容不予显示")
                    }
                }

                helper.V.text_delete doClick {
                    onArticleDelete(item)
                }
            }
            2 -> {
                helper.V.layout_content.isSelected = item.hidden
                helper.V.text_time.isVisible = !item.hidden

                if (item.hidden) {
                    helper.V.text_title.text = "Boss已下架，内容不予显示"
                    helper.V.text_name.text = item.bossName
                    GlideApp.displayHead(item.bossHead.fullUrl(), helper.V.image_head)
                } else {
                    helper.V.text_title.text = item.content
                    GlideApp.displayHead(item.bossHead.fullUrl(), helper.V.image_head)
                    helper.V.text_name.text = "${item.bossName}·${item.bossRole}"
                    helper.V.text_time.text = "${DateFormat.date2yymmdd(item.createTime)}收藏"
                }

                helper.V.layout_content doClick {
                    if (!item.hidden) {
                        onDailyClick(item)
                    } else {
                        Toasts.show("Boss已下架，内容不予显示")
                    }
                }

                helper.V.text_delete doClick {
                    onDailyDelete(item)
                }
            }
        }
    }

    abstract fun onArticleClick(item: HisFavEntity)

    abstract fun onDailyClick(item: HisFavEntity)

    abstract fun onArticleDelete(item: HisFavEntity)

    abstract fun onDailyDelete(item: HisFavEntity)
}