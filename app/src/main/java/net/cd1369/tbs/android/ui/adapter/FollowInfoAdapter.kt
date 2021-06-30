package net.cd1369.tbs.android.ui.adapter

import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_follow_info.view.image_head
import kotlinx.android.synthetic.main.item_follow_info.view.text_content
import kotlinx.android.synthetic.main.item_follow_info.view.text_hot
import kotlinx.android.synthetic.main.item_follow_info.view.text_info
import kotlinx.android.synthetic.main.item_follow_info.view.text_name
import kotlinx.android.synthetic.main.item_follow_info.view.text_time
import kotlinx.android.synthetic.main.item_follow_info.view.text_title
import kotlinx.android.synthetic.main.item_follow_photo.view.image_res
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.TestMultiEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/6/28 5:17 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class FollowInfoAdapter :
    BaseMultiItemQuickAdapter<TestMultiEntity, BaseViewHolder>(mutableListOf()) {

    init {
        addItemType(0, R.layout.item_follow_info)
        addItemType(1, R.layout.item_follow_photo)
    }

    override fun convert(helper: BaseViewHolder, item: TestMultiEntity) {
        when (helper.itemViewType) {
            0 -> {
                helper.V.text_title.text = "搞什么副业可以月入过万"
                GlideApp.display(R.drawable.ic_default_photo, helper.V.image_head)
                helper.V.text_name.text = "莉莉娅"
                helper.V.text_info.text = "灵魂莲华"
                helper.V.text_content.text =
                    "领效电提算场已将铁存它色置识种是量性传周么名光却次种中节志至或局会点再部技七条先位记建政领效电提算场已将铁存它色置识种是量性传周么名光却次种中节志至或局会点再部技七条先位记建政"
                helper.V.text_hot.text = "8.2k收藏·19.9w人围观"
                helper.V.text_time.text = "2020/5/30"
            }
            else -> {
                helper.V.text_title.text = "搞什么可以月入过入过万"
                GlideApp.display(R.drawable.ic_test_head, helper.V.image_head)
                helper.V.text_name.text = "神里凌华"
                helper.V.text_info.text = "精神信仰"
                GlideApp.display(R.drawable.ic_test_splash, helper.V.image_res)
                helper.V.text_content.text =
                    "领效电提算置识种是量政领效电提算置识种是量政领效电提算置识种是量政领效电提算置识种是量政"
                helper.V.text_hot.text = "8.2k收藏·19.9w人围观"
                helper.V.text_time.text = "2020/5/30"
            }
        }

        helper.V doClick {
            onClick(item)
        }
    }

    abstract fun onClick(item: TestMultiEntity)
}