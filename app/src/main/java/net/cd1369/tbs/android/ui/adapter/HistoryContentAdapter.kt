package net.cd1369.tbs.android.ui.adapter

import cn.wl.android.lib.utils.DateFormat
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_favorite_history_content.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.entity.HistoryEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/7/5 2:13 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class HistoryContentAdapter(val today: Boolean) :
    BaseQuickAdapter<HistoryEntity, BaseViewHolder>(R.layout.item_favorite_history_content) {

    override fun convert(helper: BaseViewHolder, item: HistoryEntity) {

        helper.V.text_title.text = item.articleTitle

        helper.V.text_name.text = item.bossName
        helper.V.text_time.text =
            if (today) DateFormat.getHHmm(item.updateTime) else DateFormat.date2yymmdd(item.updateTime)
        GlideApp.display(item.bossHead, helper.V.image_head)

        helper.V.layout_content doClick {
            onContentClick(item.articleId)
        }

        helper.V.text_delete doClick {
            onContentDelete(item.id, ::removeItem)
        }


    }

    private fun removeItem(id: String) {
        val entity = mData.first {
            it.id == id
        }
        mData.remove(entity)
        notifyDataSetChanged()
    }

    abstract fun onContentClick(articleId: String)

    abstract fun onContentDelete(
        historyId: String,
        doRemove: ((id: String) -> Unit)
    )

}