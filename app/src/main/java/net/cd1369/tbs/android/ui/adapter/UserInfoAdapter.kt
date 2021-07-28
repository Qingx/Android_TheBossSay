package net.cd1369.tbs.android.ui.adapter

import android.annotation.SuppressLint
import androidx.core.view.isInvisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_user_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/7/5 12:46 下午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class UserInfoAdapter : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_user_info) {
    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: Int) {
        val loginStatus = UserConfig.get().loginStatus
        val entity = UserConfig.get().userEntity

        val name: String
        val id: String
        val phone: String
        if (loginStatus) {
            name = entity.nickName
            id = "ID：${entity.id}"
            phone = if (entity?.phone.isNullOrEmpty()) {
                "微信用户暂无手机号"
            } else entity.phone.replaceRange(3, 11, "********")
        } else {
            name = "请先登录！"
            id = "游客：${DataConfig.get().tempId}"
            phone = "请先登录！"
        }

        when (item) {
            0 -> {
                helper.V.text_name.text = "账号昵称"
                helper.V.text_content.text = name
            }
            1 -> {
                helper.V.text_name.text = "账号ID"
                helper.V.text_content.text = id
            }
            else -> {
                helper.V.text_name.text = "登录手机号"
                helper.V.text_content.text = phone
            }
        }

        helper.V.image_change.isInvisible = item == 1

        helper.V.image_change doClick {
            onChangeClick(item)
        }

        helper.V doClick {
            onItemClick(item)
        }
    }

    abstract fun onItemClick(item: Int)

    abstract fun onChangeClick(item: Int)
}