package net.cd1369.tbs.android.ui.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import cn.wl.android.lib.miss.EmptyMiss
import cn.wl.android.lib.ui.BaseListActivity
import cn.wl.android.lib.utils.GlideApp
import com.allen.library.CircleImageView
import com.blankj.utilcode.util.ConvertUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_print_sub.*
import kotlinx.android.synthetic.main.item_print_hot.view.*
import kotlinx.android.synthetic.main.layout_sub_header.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.PrintSubModel
import net.cd1369.tbs.android.ui.recommend.adapter.PrintSubAdapter
import net.cd1369.tbs.android.util.ALCall
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 11:00 2021/12/21
 * @desc
 */
class PrintSubActivity : BaseListActivity() {

    private var mSubjectId: String = ""
    private var mHeaderView: View? = null
    private var mSubAdapter: PrintSubAdapter? = null

    private val mSize by lazy { ConvertUtils.dp2px(24F) }
    private val mPadding by lazy { ConvertUtils.dp2px(8F) }

    companion object {
        fun start(context: Context, id: String) {
            val starter = Intent(context, PrintSubActivity::class.java)
                .putExtra("id", id)
            context.startActivity(starter)
        }
    }

    override fun getLayoutResource(): Any =
        R.layout.activity_print_sub

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mSubjectId = intent.getStringExtra("id") ?: ""
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? =
        PrintSubAdapter().also { mSubAdapter = it }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        iv_back doClick { onBackPressed() }

        tv_title_name.alpha = 0F

        abl_layout.addOnOffsetChangedListener(object : ALCall() {
            override fun onStateChanged(
                appBarLayout: AppBarLayout,
                collapsed: State,
                progress: Float
            ) {
                tv_title.alpha = 1F - (1.8F * progress).coerceAtMost(1F)
                tv_title_name.alpha = progress
            }
        })

        addHeaderView()
    }

    private fun addHeaderView() {
        mHeaderView = layoutInflater.inflate(R.layout.layout_sub_header, null).also {
            mSubAdapter?.addHeaderView(it)
        }
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        TbsApi.boss().obtainPrintSub(mSubjectId, pageParam)
            .map {
                runOnUiThread { tryUpdateHeadData(it) }

                return@map it.articleDetailsVOPage
            }
            .map {
                if (it.records.isNullOrEmpty()) {
                    throw EmptyMiss()
                }
                return@map it
            }
            .bindPageSubscribe(loadMore) {
                if (loadMore) {
                    mSubAdapter?.addData(it)
                } else {
                    mSubAdapter?.setNewData(it)
                }
            }
    }

    private fun tryUpdateHeadData(model: PrintSubModel) {
        tv_title.text = model.subjectName
        tv_title_name.text = model.subjectName

        GlideApp.display(model.background.fullUrl(), iv_print_img)

        mHeaderView?.also {
            it.tv_sub_content.text = model.introduction
            it.tv_sub_count.text = "共收录${model.articleNum}篇文章"
            it.tv_sub_desc.text = "等${model.bosses?.size ?: 0}位大佬参与"

            addBossAvatars(it.ll_sub_boss, model.bosses)
        }
    }

    private fun addBossAvatars(root: LinearLayoutCompat, bosses: List<BossInfoEntity>?) {
        root.removeAllViews()
        bosses ?: return

        temp@
        for ((index, boss) in bosses.withIndex()) {
            if (index > 8) break@temp

            val avatarView = createAvatar()
            tryAddBossAvatar(
                root, avatarView, boss.head.fullUrl() ?: ""
            )
        }
    }

    /**
     *
     * @param root LinearLayout
     * @param civ View
     */
    private fun tryAddBossAvatar(root: LinearLayoutCompat, civ: View, path: String) {
        if (civ !is CircleImageView) return

        (civ.parent as? ViewGroup)?.removeView(civ)

        val layoutParams = LinearLayoutCompat.LayoutParams(mSize, mSize)
        layoutParams.leftMargin = -mPadding

        root.addView(civ, layoutParams)
        GlideApp.display(path, civ)
    }

    private fun createAvatar(): View {
        return layoutInflater.inflate(
            R.layout.layout_boss_avatar, null
        )
    }

}