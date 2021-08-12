package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Toasts
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_mine_collect_article.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.adapter.FavoriteAdapter
import net.cd1369.tbs.android.ui.dialog.AddFolderDialog
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.max

class MineCollectArticleActivity : BaseActivity() {
    private lateinit var mAdapter: FavoriteAdapter
    private var needLoading = true
    private var totalNum = UserConfig.get().userEntity.collectNum

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, MineCollectArticleActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_mine_collect_article
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        eventBus.register(this)

        text_title.text = "我的收藏($totalNum)"

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData()
        }

        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        mAdapter = object : FavoriteAdapter() {
            override fun onContentItemClick(entity: ArticleEntity) {
                ArticleActivity.start(mActivity, entity.id)
            }

            override fun onItemDelete(folderId: String) {
                removeFolder(folderId)
            }

            override fun onContentItemDelete(articleId: String, doRemove: (id: String) -> Unit) {
                removeArticle(articleId, doRemove)
            }

        }
        rv_content.adapter = mAdapter

        button_float doClick {
            AddFolderDialog.showDialog(supportFragmentManager).apply {
                this.onConfirmClick = AddFolderDialog.OnConfirmClick {
                    createFolder(it, this)
                }
            }
        }

        image_back doClick {
            onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun loadData() {
        super.loadData()
        if (needLoading) showLoading()

        TbsApi.user().obtainFavoriteList()
            .onErrorReturn { mutableListOf() }
            .bindDefaultSub(doNext = {
                val flatMap: List<ArticleEntity> = it.flatMap { e ->
                    e.list ?: mutableListOf()
                }
                totalNum = flatMap.size

                text_title.text = "我的收藏($totalNum)"

                mAdapter.setNewData(it)
            }, doDone = {
                showContent()
                layout_refresh.finishRefresh()
            })
    }

    private fun createFolder(name: String, dialog: AddFolderDialog) {
        showLoadingAlert("尝试新建...")

        TbsApi.user().obtainCreateFavorite(name)
            .bindDefaultSub(doNext = {
                mAdapter.addData(it)
                dialog.dismiss()
            }, doDone = {
                hideLoadingAlert()
            }, doFail = {
                Toasts.show("新建失败")
            })
    }

    @SuppressLint("SetTextI18n")
    private fun removeFolder(id: String) {
        showLoadingAlert("尝试删除...")

        TbsApi.user().obtainRemoveFolder(id)
            .bindDefaultSub(doNext = {
                val position = mAdapter?.data.indexOfFirst {
                    it.id == id
                }
                totalNum = totalNum!! - (mAdapter.data[position].list?.size ?: 0)
                text_title.text = "我的收藏($totalNum)"

                mAdapter.remove(position)

                UserConfig.get().updateUser {
                    it.collectNum = max(totalNum ?: 0, 0)
                }
                eventBus.post(RefreshUserEvent())

                Toasts.show("删除成功")
            }, doFail = {
                Toasts.show("删除失败，${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    @SuppressLint("SetTextI18n")
    private fun removeArticle(id: String, doRemove: (id: String) -> Unit) {
        showLoadingAlert("尝试取消收藏...")

        TbsApi.user().obtainCancelFavoriteArticle(id)
            .bindDefaultSub(doNext = {
                doRemove.invoke(id)

                totalNum = totalNum!! - 1
                text_title.text = "我的收藏($totalNum)"

                UserConfig.get().updateUser {
                    it.collectNum = max((it.collectNum ?: 0) - 1, 0)
                }

                eventBus.post(RefreshUserEvent())

                Toasts.show("取消成功")
            }, doFail = {
                Toasts.show("取消失败")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: RefreshUserEvent) {
        loadData()
    }
}