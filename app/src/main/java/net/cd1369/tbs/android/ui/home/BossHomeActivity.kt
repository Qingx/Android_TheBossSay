package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
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
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.model.TestMultiEntity
import net.cd1369.tbs.android.event.FollowBossEvent
import net.cd1369.tbs.android.ui.adapter.FollowInfoAdapter
import net.cd1369.tbs.android.util.avatar
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

class BossHomeActivity : BaseListActivity() {
    private lateinit var mAdapter: FollowInfoAdapter
    private var needLoading = true
    private lateinit var entity: BossInfoEntity

    companion object {
        fun start(context: Context?, entity: BossInfoEntity) {
            val intent = Intent(context, BossHomeActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtras(Bundle().apply {
                        putSerializable("entity", entity)
                    })
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_boss_home
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        entity = intent.getSerializableExtra("entity") as BossInfoEntity
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        eventBus.register(this)

        setUserInfo()

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData(false)
        }

        rv_content.adapter = mAdapter
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
            BossInfoActivity.start(mActivity, entity)
        }
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : FollowInfoAdapter() {
            override fun onClick(item: ArticleEntity) {
                Toasts.show(item.id)
            }
        }.also {
            mAdapter = it
        }
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()

            if (needLoading) showLoading()
        }

        TbsApi.boss().obtainBossArticleList(pageParam, entity.id)
            .onErrorReturn {
                Page.empty()
            }.bindPageSubscribe(loadMore = loadMore, doNext = {
                if (loadMore) mAdapter.addData(it)
                else mAdapter.setNewData(it)
            }, doDone = {
                showContent()

                layout_refresh.finishRefresh()
                layout_refresh.finishLoadMore()
            })
    }

    private fun refreshBoss() {
        TbsApi.boss().obtainBossDetail(entity.id)
            .bindDefaultSub(doNext = {
                entity = it
                setUserInfo()
            })
    }

    @SuppressLint("SetTextI18n")
    private fun setUserInfo() {
        GlideApp.display(entity.head.avatar(), image_head, R.drawable.ic_default_photo)
        text_name.text = entity.name
        text_info.text = entity.role
        text_label.text = "${entity.collect ?: 0}万阅读·${entity.totalCount ?: 0}185篇言论"
        text_follow.text = if (entity.isCollect) "已追踪" else "追踪"
        text_follow.isSelected = entity.isCollect
        text_content.text =
            "个人简介：${entity.info}"
        text_num.text = "共${entity.totalCount}篇"
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: FollowBossEvent) {
        refreshBoss()
    }
}