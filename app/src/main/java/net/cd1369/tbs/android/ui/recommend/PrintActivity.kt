package net.cd1369.tbs.android.ui.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import cn.wl.android.lib.core.ErrorBean
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.ui.BaseListFragment
import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.view.holder.BaseHolder
import com.blankj.utilcode.util.ConvertUtils
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_recommend_hot.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.util.ALCall
import net.cd1369.tbs.android.util.doClick

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 20:04 2021/12/16
 * @desc
 */
class PrintActivity : BaseActivity() {

    private var mListFragment: BaseListFragment? = null
    private var mSubjectId: String = ""
    private var isSumTitle: Boolean = false

    companion object {

        /**
         *
         * @param context Context
         * @param id String
         * @param sum Boolean
         */
        fun start(context: Context, id: String, sum: Boolean) {
            val starter = Intent(
                context,
                PrintActivity::class.java
            )
            starter.putExtra("id", id)
            starter.putExtra("sum", sum)
            context.startActivity(starter)
        }

    }

    override fun getLayoutResource(): Any =
        R.layout.activity_recommend_hot

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mSubjectId = intent.getStringExtra("id") ?: ""
        isSumTitle = intent.getBooleanExtra("sum", false)
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
//        setHolderBackgroundColor(R.color.white)
        iv_back doClick { onBackPressed() }
        tv_desc.text = "聚焦话题，深度理解"
        tv_title.text = "热点专题"
        tv_title_name.text = "热点专题"

        tv_title_name.alpha = 0F

        abl_layout.addOnOffsetChangedListener(object : ALCall() {
            override fun onStateChanged(
                appBarLayout: AppBarLayout,
                collapsed: State,
                progress: Float
            ) {
                tv_desc.alpha = 1F - (1.8F * progress).coerceAtMost(1F)
                tv_title.alpha = 1F - (1.8F * progress).coerceAtMost(1F)
                tv_title_name.alpha = progress
            }
        })

        vp_content.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getCount(): Int = 1
            override fun getItem(position: Int): Fragment = if (isSumTitle) {
                PrintHotFragment.newIns(mSubjectId).also {
                    mListFragment = it
                }
            } else {
                PrintCommonFragment.newIns(mSubjectId).also {
                    mListFragment = it
                }
            }
        }
    }

    override fun loadData() {
        super.loadData()

        if (mListFragment?.isEmptyAdapter != false) {
            showLoading()
        }

        TbsApi.boss().obtainPrintDetails(mSubjectId)
            .bindSubscribe(doDone = {
                mListFragment?.hideLoadingAlert()
                mListFragment?.tryFinishRefresh()
            }) {
                val list = it.smallSubjectIntroduction

                GlideApp.display(it.cover, iv_print_img)

                tv_desc.text = it.introduction
                tv_title.text = it.subjectName
                tv_title_name.text = it.subjectName

                if (list.isNullOrEmpty()) {
                    val bean = ErrorBean()
                    bean.icon = R.drawable.ic_empty_search
                    bean.msg = "暂无专题数据"

                    showEmptyData(bean)
                } else {
                    showContent()

//                    list.addAll(list.toMutableList().shuffled())
//                    list.addAll(list.toMutableList().shuffled())
                    mListFragment?.let { fragment ->
                        if (fragment is OnPrintConsumer) {
                            fragment.doNext(list)
                        }
                    }
                }
            }
    }

}