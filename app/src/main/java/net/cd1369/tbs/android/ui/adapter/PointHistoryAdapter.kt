package net.cd1369.tbs.android.ui.adapter

import cn.wl.android.lib.utils.DateFormat
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_point_history.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.HistoryEntity
import net.cd1369.tbs.android.data.entity.PointEntity
import net.cd1369.tbs.android.data.entity.PortEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/7/5 2:13 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class PointHistoryAdapter() :
    BaseQuickAdapter<PointEntity, BaseViewHolder>(R.layout.item_point_history) {

    override fun convert(helper: BaseViewHolder, item: PointEntity) {
        helper.V.text_title.text = item.articleTitle
        helper.V.text_delete.text = "删除"

        helper.V.text_name.text = item.bossName
        helper.V.text_time.text = DateFormat.date2yymmdd(item.createTime)
        GlideApp.display(item.bossHead, helper.V.image_head)

        helper.V.layout_content doClick {
            onContentClick(item.articleId)
        }

        helper.V.text_delete doClick {
            onContentDelete(item.articleId, ::removeItem)
        }
    }

    private fun removeItem(id: String) {
        val entity = mData.firstOrNull() {
            it.articleId == id
        } ?: return

        mData.remove(entity)
        notifyDataSetChanged()
    }

    abstract fun onContentClick(articleId: String)

    abstract fun onContentDelete(
        historyId: String,
        doRemove: ((id: String) -> Unit)
    )

}