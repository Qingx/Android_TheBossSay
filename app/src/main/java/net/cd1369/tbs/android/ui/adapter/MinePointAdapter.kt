package net.cd1369.tbs.android.ui.adapter

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import cn.wl.android.lib.utils.DateFormat
import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_point_article.view.*
import kotlinx.android.synthetic.main.item_point_daily.view.text_content
import kotlinx.android.synthetic.main.item_point_daily.view.text_name
import kotlinx.android.synthetic.main.item_point_daily.view.image_head
import kotlinx.android.synthetic.main.item_point_daily.view.text_delete
import kotlinx.android.synthetic.main.item_point_daily.view.layout_content
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.HisFavEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl

/**
 * Created by Xiang on 2021/10/21 18:14
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class MinePointAdapter :
    BaseMultiItemQuickAdapter<HisFavEntity, BaseViewHolder>(mutableListOf()) {
    init {
        addItemType(1, R.layout.item_point_article)
        addItemType(2, R.layout.item_point_daily)
    }

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: HisFavEntity) {
        helper.V.isSelected = item.hidden

        if (item.hidden) {
            GlideApp.display(
                R.drawable.ic_article_cover,
                helper.V.image_cover,
                R.drawable.ic_article_cover
            )
            helper.V.text_content.text = "Boss已下架，内容不予显示"
            GlideApp.displayHead(item.bossHead.fullUrl(), helper.V.image_head)
            helper.V.text_name.text = item.bossName
        } else {
            if (helper.itemViewType == 1) {
                helper.V.text_time.isVisible = true

                GlideApp.display(
                    item.cover.fullUrl(),
                    helper.V.image_cover,
                    R.drawable.ic_article_cover
                )
                helper.V.text_content.text = item.content
                GlideApp.displayHead(item.bossHead.fullUrl(), helper.V.image_head)
                helper.V.text_name.text = item.bossName
                helper.V.text_time.text = DateFormat.date2yymmdd(item.createTime)
                helper.V.layout_talk.isVisible = !item.hidden && item.articleType == "1"
                helper.V.layout_msg.isVisible = !item.hidden && item.articleType == "2"
            } else {
                helper.V.text_content.text = item.content
                GlideApp.displayHead(item.bossHead.fullUrl(), helper.V.image_head)
                helper.V.text_name.text = item.bossName
            }
        }

        helper.V.text_delete doClick {
            onContentDelete(item, ::removeItem)
        }

        helper.V.layout_content doClick {
            if (!item.hidden) {
                onContentClick(item)
            } else {
                Toasts.show("Boss已下架，内容不予显示")
            }
        }
    }

    private fun removeItem(id: String) {
        val entity = mData.firstOrNull() {
            it.articleId == id
        } ?: return

        mData.remove(entity)
        notifyDataSetChanged()
    }

    abstract fun onContentClick(entity: HisFavEntity)

    abstract fun onContentDelete(
        entity: HisFavEntity,
        doRemove: ((id: String) -> Unit)
    )
}