package net.cd1369.tbs.android.ui.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.GridLayoutManager
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_follow_photo.view.*
import kotlinx.android.synthetic.main.item_square_info.view.*
import kotlinx.android.synthetic.main.item_square_info.view.image_res
import kotlinx.android.synthetic.main.item_square_info.view.text_info
import kotlinx.android.synthetic.main.item_square_info.view.text_title
import kotlinx.android.synthetic.main.item_square_photo.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.TestMultiEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick


/**
 * Created by Qing on 2021/6/30 10:36 上午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class SquareInfoAdapter :
    BaseMultiItemQuickAdapter<TestMultiEntity, BaseViewHolder>(mutableListOf()) {
    init {
        addItemType(0, R.layout.item_square_info)
        addItemType(1, R.layout.item_square_photo)
    }

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: TestMultiEntity) {
        when (helper.itemViewType) {
            0 -> {
                helper.V.text_title.text = "搞什么副业可以月入过万搞什么副业可以月入过万搞什么副业可以月入过万搞什么副业可以月入过万"
                GlideApp.display(R.drawable.ic_default_photo, helper.V.image_res)
                helper.V.text_info.text = "烽火崛起·19.9w人围观·6小时前"
            }
            else -> {
                helper.V.text_title.text = "继法国之后，德国也宣布不承认中国疫苗，接种者或将被拒绝入境接种者或将被拒绝入境"
                val adapter = GridImageAdapter()
                helper.V.rv_content.adapter = adapter
                adapter.setNewData(mutableListOf(0, 1, 2))
                helper.V.rv_content.layoutManager = object : GridLayoutManager(mContext, 3) {
                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }

                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
                helper.V.text_info.text = "广告·海南万科"
            }
        }

        helper.V doClick {
            onClick(item)
        }
    }

    abstract fun onClick(item: TestMultiEntity)
}