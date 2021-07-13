package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseListActivity
import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_boss_home.*
import kotlinx.android.synthetic.main.activity_boss_home.layout_refresh
import kotlinx.android.synthetic.main.activity_boss_home.rv_content
import kotlinx.android.synthetic.main.activity_boss_home.text_num
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.model.TestMultiEntity
import net.cd1369.tbs.android.ui.adapter.FollowInfoAdapter
import net.cd1369.tbs.android.util.doClick
import java.util.concurrent.TimeUnit

class BossHomeActivity : BaseListActivity() {
    private lateinit var mAdapter: FollowInfoAdapter
    private var needLoading = true
    companion object{
        fun start(context: Context?) {
            val intent = Intent(context, BossHomeActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_boss_home
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        GlideApp.display(R.drawable.ic_default_photo, image_bg)
        GlideApp.display(R.drawable.ic_test_head, image_head)
        text_name.text = "神里凌华"
        text_info.text = "精神信仰"
        text_label.text = "19.9万阅读·185篇言论"
        text_follow.text = "已追踪"
        text_follow.isSelected = true
        text_content.text =
            "个人简介：府人都少物白类活从第有见易西世济社外断入府人都少物白类活从第有见易西世济社外断入府人都少物白类活从第有府人都少物白类活从第有见易西世济社外断入府人都少物白类活从第有见易西世济社外断入府人都少物白类活从第有…"
        text_num.text="共25篇"

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData(false)
        }

        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        image_back doClick {
            onBackPressed()
        }

        text_content doClick {
            BossInfoActivity.start(mActivity)
        }
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : FollowInfoAdapter() {
            override fun onClick(item: ArticleEntity) {
                Toasts.show("${item.content}")
            }
        }.also {
            mAdapter = it
        }
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

//        if (!loadMore && needLoading) {
//            showLoading()
//        }
//
//        val testData = mutableListOf(0, 1, 2, 3, 4, 5, 6, 7)
//        val multiData = testData.map {
//            TestMultiEntity(0,it)
//        }
//
//        Observable.just(multiData)
//            .observeOn(AndroidSchedulers.mainThread())
//            .delay(2, TimeUnit.SECONDS)
//            .bindListSubscribe {
//                showContent()
//                layout_refresh.finishRefresh()
//
//                mAdapter.setNewData(it)
//                needLoading = true
//            }
    }
}