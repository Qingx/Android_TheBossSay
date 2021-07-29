package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.core.view.isVisible
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.utils.Toasts
import kotlinx.android.synthetic.main.activity_article.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.entity.UserEntity
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.dialog.CreateFolderDialog
import net.cd1369.tbs.android.ui.dialog.SelectFolderDialog
import net.cd1369.tbs.android.ui.dialog.ShareDialog
import net.cd1369.tbs.android.ui.start.InputPhoneActivity
import net.cd1369.tbs.android.util.*
import net.cd1369.tbs.android.util.avatar

class ArticleActivity : BaseActivity() {
    private var articleId: String? = null
    private lateinit var entity: ArticleEntity
    private var isCollect: Boolean? = null

    companion object {
        fun start(context: Context?, id: String, entity: ArticleEntity = ArticleEntity.empty) {
            val intent = Intent(context, ArticleActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtras(Bundle().apply {
                        putString("articleId", id)
                        putSerializable("articleEntity", entity)
                    })
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_article
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        articleId = intent.getStringExtra("articleId") as String
        entity = intent.getSerializableExtra("articleEntity") as ArticleEntity
        isCollect = entity.isCollect
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        image_collect.isSelected = isCollect!!

        tryReadArticle(articleId!!)

        image_collect doClick {
            if (UserConfig.get().loginStatus) {
                if (isCollect!!) {
                    cancelCollect()
                } else doCollect()
            } else {
                Toasts.show("请先登录！")
                InputPhoneActivity.start(mActivity)
            }
        }

        button_float doClick {
            layout_scroll.smoothScrollTo(0, 0)
        }

        image_share doClick {
            onShare()
        }

        image_back doClick {
            onBackPressed()
        }
    }

    private fun onShare() {
        ShareDialog.showDialog(supportFragmentManager, "shareDialog")
            .apply {
                onSession = Runnable {
                    doShareSession(resources)
                }
                onTimeline = Runnable {
                    doShareTimeline(resources)
                }
                onCopyLink = Runnable {
                    Tools.copyText(mActivity, Const.SHARE_URL)
                }
            }
    }

    override fun loadData() {
        super.loadData()

        if (entity == ArticleEntity.empty) {
            showLoading()

            TbsApi.boss().obtainDetailArticle(articleId)
                .bindDefaultSub(doNext = {
                    isCollect = it.isCollect
                    entity = it

                    setInfo(it)
                }, doDone = {
                    showContent()
                })
        } else {
            setInfo(entity)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setInfo(it: ArticleEntity) {
        image_collect.isSelected = it.isCollect!!
        text_title.text = it.title
        text_info.text = "${it.collect ?: 0}k收藏·${it.point ?: 0}w浏览"
        text_time.text = getUpdateTime(it.createTime).replace("更新", "")
        GlideApp.display(it.bossVO.head.avatar(), image_head, R.drawable.ic_default_photo)
        text_name.text = it.bossVO.name
        text_role.text = it.bossVO.role
        layout_content.isSelected = it.bossVO.isCollect

        text_content.text =Html.fromHtml(it.content)
    }

    private fun cancelCollect() {
        showLoadingAlert("尝试取消...")

        TbsApi.user().obtainCancelFavoriteArticle(entity.id)
            .bindDefaultSub(doNext = {
                isCollect = false
                entity.isCollect = false
                image_collect.isSelected = false

                eventBus.post(RefreshUserEvent())

                Toasts.show("取消成功")
            }, doFail = {
                Toasts.show("取消失败，${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    private fun doCollect() {
        showLoadingAlert("获取收藏夹...")

        TbsApi.user().obtainFavoriteList()
            .onErrorReturn {
                mutableListOf()
            }.bindDefaultSub(doNext = {
                hideLoadingAlert()

                SelectFolderDialog.showDialog(supportFragmentManager, "selectFolder", it)
                    .apply select@{
                        this.onCreateClick = SelectFolderDialog.OnCreateClick {
                            CreateFolderDialog.showDialog(supportFragmentManager, "createFolder")
                                .apply create@{
                                    this.onConfirmClick =
                                        CreateFolderDialog.OnConfirmClick { name ->
                                            showLoadingAlert("尝试收藏...")

                                            TbsApi.user().obtainCreateFavorite(name)
                                                .flatMap { folder ->

                                                    TbsApi.user()
                                                        .obtainFavoriteArticle(folder.id, entity.id)
                                                }.bindDefaultSub(doNext = {
                                                    isCollect = true
                                                    entity.isCollect = true
                                                    this@ArticleActivity.image_collect.isSelected =
                                                        true

                                                    eventBus.post(RefreshUserEvent())

                                                    Toasts.show("收藏成功")
                                                    this@create.dismiss()
                                                    this@select.dismiss()
                                                }, doFail = { error ->
                                                    Toasts.show("收藏失败，${error.msg}")
                                                }, doDone = {
                                                    hideLoadingAlert()
                                                })
                                        }
                                }
                        }
                        this.onConfirmClick = SelectFolderDialog.OnConfirmClick { folderId ->
                            showLoadingAlert("尝试收藏...")

                            TbsApi.user().obtainFavoriteArticle(folderId, entity.id)
                                .bindDefaultSub(doNext = {
                                    isCollect = true
                                    entity.isCollect = true
                                    this@ArticleActivity.image_collect.isSelected = true

                                    Toasts.show("收藏成功")

                                    this.dismiss()
                                }, doFail = { error ->
                                    Toasts.show("收藏失败，${error.msg}")
                                }, doDone = {
                                    hideLoadingAlert()
                                })
                        }
                    }
            })
    }

    /**
     * 尝试阅读记录文章
     * @param articleId String
     */
    private fun tryReadArticle(articleId: String) {
        TbsApi.user().obtainReadArticle(articleId)
            .bindDefaultSub {
                eventBus.post(RefreshUserEvent())
            }
    }
}