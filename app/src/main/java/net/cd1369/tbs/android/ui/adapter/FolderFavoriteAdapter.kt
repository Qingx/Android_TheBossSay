package net.cd1369.tbs.android.ui.adapter

import android.annotation.SuppressLint
import androidx.core.view.isInvisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_folder_favorite.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.FavoriteEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Xiang on 2021/7/15 14:38
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class FolderFavoriteAdapter :
    BaseQuickAdapter<FavoriteEntity, BaseViewHolder>(R.layout.item_folder_favorite) {
    private var mSelectIndex = 0

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: FavoriteEntity) {
        helper.V.isSelected = mSelectIndex == helper.layoutPosition
        helper.V.image_select.isInvisible = mSelectIndex != helper.layoutPosition

        helper.V.text_name.text = item.name

        helper.V.text_content.text = "${item.list?.size ?: 0}篇言论"

        helper.V doClick {
            if (mSelectIndex != helper.layoutPosition) {
                val lastIndex = mSelectIndex
                mSelectIndex = helper.layoutPosition

                notifyItemChanged(lastIndex)
                notifyItemChanged(mSelectIndex)

                onItemClick(item.id)
            }
        }
    }

    abstract fun onItemClick(id: String)
}