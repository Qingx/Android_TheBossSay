package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Toasts
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_mine_collect_article.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.FolderEntity
import net.cd1369.tbs.android.event.ArticleCollectEvent
import net.cd1369.tbs.android.ui.adapter.FolderAdapter
import net.cd1369.tbs.android.ui.dialog.CreateFolderDialog
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.max

class MineCollectActivity : BaseActivity() {
    private var needLoading: Boolean = true
    private var collectNum: Int? = null
    private lateinit var mAdapter: FolderAdapter

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, MineCollectActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_mine_collect_article
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        collectNum = UserConfig.get().userEntity.collectNum
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        eventBus.register(this)

        text_title.text = "我的收藏（${collectNum}）"

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData()
        }

        rv_content.layoutManager = object : LinearLayoutManager(mActivity) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        mAdapter = object : FolderAdapter() {
            override fun onItemClick(item: FolderEntity?) {
                if (item != null) {
                    MineCollectContentActivity.start(mActivity, item)
                } else {
                    Toasts.show("收藏夹为空")
                }
            }

            override fun onDeleteClick(item: FolderEntity) {
                tryDeleteFolder(item)
            }
        }
        rv_content.adapter = mAdapter

        image_back doClick {
            onBackPressed()
        }

        button_float doClick {
            tryCreateFolder()
        }
    }

    private fun tryCreateFolder() {
        CreateFolderDialog.showDialog(supportFragmentManager, "createFolder")
            .apply {
                onConfirmClick = CreateFolderDialog.OnConfirmClick { name ->
                    TbsApi.user().obtainCreateFavorite(name).bindDefaultSub({
                        Toasts.show("创建失败")
                    }, {
                        this?.dismiss()
                    }, {
                        val folder = FolderEntity(it.id, name, "", 0, 0, it.createTime)
                        mAdapter.addData(folder)
                    })
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun tryDeleteFolder(item: FolderEntity) {
        showLoadingAlert("尝试删除收藏夹...")

        TbsApi.user().obtainRemoveFolder(item.id)
            .bindDefaultSub {
                val index = mAdapter.data.indexOfFirst {
                    it.id == item.id
                }

                if (index > -1) {
                    mAdapter.remove(index)

                    UserConfig.get().updateUser {
                        it.collectNum = max((it.collectNum ?: 0) - item.articleCount, 0)
                    }
                    collectNum = max((collectNum ?: 0) - item.articleCount, 0)
                    text_title.text = "我的收藏（${collectNum}）"

                    eventBus.post(ArticleCollectEvent(fromCollect = true))
                }
            }
    }

    override fun loadData() {
        super.loadData()

        if (needLoading) {
            showLoading()
        }

        TbsApi.user().obtainFolderList().bindDefaultSub(null, {
            layout_refresh.finishRefresh()
            showContent()
        }, {
            mAdapter.setNewData(it)
        })
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: ArticleCollectEvent) {
        if (!event.fromCollect) {
            layout_refresh.autoRefresh()
            collectNum = UserConfig.get().userEntity.collectNum
            text_title.text = "我的收藏（${collectNum}）"
        }
    }
}