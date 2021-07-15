package net.cd1369.tbs.android.ui.adapter

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_favorite.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.FavoriteEntity
import net.cd1369.tbs.android.data.model.TestFavoriteEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/7/5 2:21 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class FavoriteAdapter :
    BaseQuickAdapter<FavoriteEntity, BaseViewHolder>(R.layout.item_favorite) {
    private val mSelect = mutableListOf<String>()

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: FavoriteEntity) {
        helper.V.rv_content.isVisible = mSelect.contains(item.id)

        helper.V.text_name.text = item.name

        helper.V.text_content.text = "${item.list?.size ?: 0}篇言论"

        helper.V.rv_content.layoutManager =
            object : LinearLayoutManager(mContext, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        val adapter = object : FavoriteContentAdapter() {
            override fun onContentClick(articleId: String) {
                onContentItemClick(articleId)
            }

            override fun onContentDelete(
                articleId: String,
                doRemove: (id: String) -> Unit
            ) {
                onContentItemDelete(articleId, doRemove)
            }
        }

        helper.V.rv_content.adapter = adapter
        adapter.setNewData(item.list ?: mutableListOf())

        helper.V.layout_content doClick {
            if (mSelect.contains(item.id)) {
                mSelect.remove(item.id)
            } else {
                mSelect.add(item.id)
            }
            notifyItemChanged(helper.layoutPosition)
        }

        helper.V.text_delete doClick {
            onItemDelete(item.id)
        }
    }

    abstract fun onContentItemClick(articleId: String)

    abstract fun onItemDelete(folderId: String)

    abstract fun onContentItemDelete(articleId: String, doRemove: (id: String) -> Unit)
}