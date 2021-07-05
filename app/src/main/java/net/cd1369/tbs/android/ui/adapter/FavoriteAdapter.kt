package net.cd1369.tbs.android.ui.adapter

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_favorite.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.TestFavoriteEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/7/5 2:21 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class FavoriteAdapter :
    BaseQuickAdapter<TestFavoriteEntity, BaseViewHolder>(R.layout.item_favorite) {
    private val mSelect = mutableListOf<Int>()

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: TestFavoriteEntity) {
        helper.V.rv_content.isVisible = mSelect.contains(item.code)

        helper.V.text_name.text = if (item.code == 0) {
            "默认收藏夹"
        } else "收藏夹${item.code}"

        helper.V.text_content.text = "${item.data.size}篇言论"

        val adapter = object : FahiContentAdapter(item.code, ::onRemoveCallback) {
            override fun onContentClick(contentItem: Int) {
                onContentItemClick(contentItem)
            }

            override fun onContentDelete(contentItem: Int, onRemove: (item: Int) -> Unit) {
                onContentItemDelete(contentItem, onRemove)
            }
        }

        helper.V.rv_content.layoutManager =
            object : LinearLayoutManager(mContext, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        helper.V.rv_content.adapter = adapter
        adapter.setNewData(item.data)

        helper.V.layout_content doClick {
            if (mSelect.contains(item.code)) {
                mSelect.remove(item.code)
            } else {
                mSelect.add(item.code)
            }
            notifyItemChanged(helper.layoutPosition)
        }

        helper.V.text_delete doClick {
            onItemDelete(item.code, ::removeFolder)
        }
    }

    abstract fun onContentItemClick(contentItem: Int)

    abstract fun onItemDelete(parentItem: Int, onRemove: ((item: Int) -> Unit))

    abstract fun onContentItemDelete(
        contentItem: Int,
        onRemove: ((item: Int) -> Unit)
    )

    fun onRemoveCallback(parentItem: Int, contentItem: Int) {
        val position = mData.indexOfFirst {
            it.code == parentItem
        }

        if (position != -1) {
            mData[position].data.remove(contentItem)
            notifyItemChanged(position)
        }
    }

    private fun removeFolder(parentItem: Int) {
        val position = mData.indexOfFirst {
            it.code == parentItem
        }

        if (position != -1) {
            remove(position)
        }
    }

    fun addFolder(item: TestFavoriteEntity) {
        addData(item)
    }

    override fun setNewData(data: MutableList<TestFavoriteEntity>?) {
        super.setNewData(data)
        mSelect.clear()
    }
}