package net.cd1369.tbs.android.ui.recommend.adapter

import android.widget.ImageView
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.RecommendEntity
import net.cd1369.tbs.android.ui.recommend.PrintActivity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 17:50 2021/12/16
 * @desc
 */
class RecommendCardAdapter :
    BaseQuickAdapter<RecommendEntity, BaseViewHolder>(R.layout.item_recommend_card) {

    override fun convert(helper: BaseViewHolder, item: RecommendEntity) {
        val iv = helper.getView<ImageView>(R.id.iv_card_img)
        GlideApp.display(item.background.fullUrl(), iv)

        helper.V doClick {
            when (item.type) {
                0 -> PrintActivity.start(it.context, item.id, true)
                1 -> PrintActivity.start(it.context, item.id, false)
            }
        }
    }

}