package net.cd1369.tbs.android.ui.recommend.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.updatePadding
import cn.wl.android.lib.utils.GlideApp
import com.allen.library.CircleImageView
import com.blankj.utilcode.util.ConvertUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_print_hot.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.PrintItemEntity
import net.cd1369.tbs.android.ui.recommend.PrintSubActivity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl
import net.cd1369.tbs.android.widget.LabelDrawable
import java.lang.StringBuilder
import java.util.*

class PrintHotAdapter : BaseQuickAdapter<PrintItemEntity, BaseViewHolder>(R.layout.item_print_hot) {

    private val viewCache: LinkedList<View> = LinkedList<View>()
    private val mSize by lazy { ConvertUtils.dp2px(24F) }
    private val mPadding by lazy { ConvertUtils.dp2px(8F) }

    override fun convert(helper: BaseViewHolder, item: PrintItemEntity) {
        val background = helper.V.ll_print_label.background
        if (background == null) {
            helper.V.ll_print_label.background = LabelDrawable()
        }

        helper.V.tv_print_title.text = item.subjectName
        helper.V.tv_print_count.text = "收录${item.articleNum}篇文章"
        helper.V.tv_print_desc.text = item.introduction

        if (helper.V.ll_boss_root.paddingLeft != mPadding) {
            helper.V.ll_boss_root.updatePadding(left = mPadding)
        }

        GlideApp.display(item.cover, helper.getView(R.id.iv_print_img))

        val articleVOs = item.articleVOs ?: mutableListOf()

        if (helper.V.ll_boss_root.childCount > 0) {
            helper.V.ll_boss_root.removeAllViews()
        }

        val sb = StringBuilder()

        temp@
        for ((index, articleVO) in articleVOs.withIndex()) {
            if (index > 8) break@temp

            if (index < 3) {
                if (index > 0) {
                    sb.append(',')
                        .append(articleVO.bossName ?: "")
                } else {
                    sb.append(articleVO.bossName ?: "")
                }
            }

            val avatarView = getOrCreateAvatar(helper.V.context)
            tryAddBossAvatar(
                helper.V.ll_boss_root, avatarView,
                articleVO?.head?.fullUrl() ?: ""
            )
        }

        sb.append("等")
            .append(articleVOs?.size ?: "0")
            .append("位大佬参与")

        helper.V.tv_boss_desc.text = sb.toString()

        helper.V doClick {
            PrintSubActivity.start(it.context, item.smallSubjectId)
        }
    }

    /**
     *
     * @param root LinearLayout
     * @param civ View
     */
    private fun tryAddBossAvatar(root: LinearLayout, civ: View, path: String) {
        if (civ !is CircleImageView) return

        (civ.parent as? ViewGroup)?.removeView(civ)

        val layoutParams = LinearLayout.LayoutParams(mSize, mSize)
        layoutParams.leftMargin = -mPadding

        root.addView(civ, layoutParams)
        GlideApp.display(path, civ)
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)

        val llBossRoot = holder.V.ll_boss_root
        val views = llBossRoot.children.toMutableList()
        llBossRoot.removeAllViews()

        viewCache.addAll(views)
        Log.e("onViewRecycled", viewCache.size.toString())
    }

    private fun getOrCreateAvatar(context: Context): View {
        var temp = viewCache.removeFirstOrNull()
        return temp ?: createAvatar(context)
    }

    private fun createAvatar(context: Context): View {
        return mLayoutInflater.inflate(
            R.layout.layout_boss_avatar, null
        )
    }

}