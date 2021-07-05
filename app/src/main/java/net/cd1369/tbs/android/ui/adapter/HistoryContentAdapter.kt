package net.cd1369.tbs.android.ui.adapter

import androidx.core.view.isVisible
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_favorite_history_content.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/7/5 5:36 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class HistoryContentAdapter :
    BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_favorite_history_content) {
    override fun convert(helper: BaseViewHolder, item: Int) {
        val hasIndex = helper.layoutPosition % 2 == 0

        helper.V.line_gray.isVisible = helper.layoutPosition != 0

        helper.V.text_title.text =
            if (hasIndex) "搞什么副业可以月入过万搞什么副业可以月入过万搞什么副业可以月入过万搞什么副业" else "搞什么副业可以月入过万搞什么副业可以月入过万"

        helper.V.text_name.text = if (hasIndex) "莉莉娅$item" else "神里凌华$item"
        helper.V.text_time.text = "2021/07/04"

        if (hasIndex) {
            GlideApp.display(
                R.drawable.ic_default_photo, helper.V.image_head
            )
        } else GlideApp.display(
            R.drawable.ic_test_head, helper.V.image_head
        )

        helper.V.text_delete doClick {
            onDeleteClick(item, ::onRemove)
        }

        helper.V doClick {
            onItemClick(item)
        }
    }

    abstract fun onDeleteClick(item: Int, doRemove: ((item: Int) -> Unit))

    abstract fun onItemClick(item: Int)

    fun onRemove(item: Int) {
        val position = mData.indexOfFirst {
            it == item
        }

        if (position != -1) {
            remove(position)
        }
    }
}