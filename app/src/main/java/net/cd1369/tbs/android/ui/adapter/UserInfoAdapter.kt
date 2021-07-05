package net.cd1369.tbs.android.ui.adapter

import androidx.core.view.isInvisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_user_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/7/5 12:46 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class UserInfoAdapter : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_user_info) {
    override fun convert(helper: BaseViewHolder, item: Int) {
        when (item) {
            0 -> {
                helper.V.text_name.text = "账号昵称"
                helper.V.text_content.text = "清和"
            }
            1 -> {
                helper.V.text_name.text = "账号ID"
                helper.V.text_content.text = "ID：101010101"
            }
            else -> {
                helper.V.text_name.text = "登录手机号"
                helper.V.text_content.text = "177****5329"
            }
        }

        helper.V.image_change.isInvisible = item == 1

        helper.V.image_change doClick {
            onItemClick(item)
        }
    }

    abstract fun onItemClick(item: Int)

    fun onChanged() {
        notifyDataSetChanged()
    }
}