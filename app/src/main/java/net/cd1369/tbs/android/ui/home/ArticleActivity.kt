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
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.dialog.CancelFollowDialog
import net.cd1369.tbs.android.ui.dialog.CreateFolderDialog
import net.cd1369.tbs.android.ui.dialog.SelectFolderDialog
import net.cd1369.tbs.android.ui.dialog.SuccessFollowDialog
import net.cd1369.tbs.android.ui.start.InputPhoneActivity
import net.cd1369.tbs.android.util.avatar
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.getUpdateTime
import net.cd1369.tbs.android.util.jumpSysShare

class ArticleActivity : BaseActivity() {
    private var articleId: String? = null
    private var isCollect: Boolean? = null
    private lateinit var entity: ArticleEntity

    companion object {
        fun start(context: Context?, id: String, isCollect: Boolean) {
            val intent = Intent(context, ArticleActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("id", id)
                    putExtra("isCollect", isCollect)
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_article
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        articleId = intent.getStringExtra("id") as String
        isCollect = intent.getBooleanExtra("isCollect", false)
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        image_collect.isSelected = isCollect!!

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

        layout_follow doClick {
            if (entity.bossVO.isCollect) {
                cancelFavorite()
            } else doFavorite()
        }

        button_float doClick {
            layout_scroll.smoothScrollTo(0, 0)
        }

        image_share doClick {
            jumpSysShare(mActivity, "https://www.bilibili.com/")
        }
    }

    override fun loadData() {
        super.loadData()

        showLoading()

        TbsApi.boss().obtainDetailArticle(articleId)
            .bindDefaultSub(doNext = {
                isCollect = it.isCollect
                entity = it

                setInfo(it)
            }, doDone = {
                showContent()
            })
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
        image_add.isVisible = !it.bossVO.isCollect
        text_follow.text = if (it.bossVO.isCollect) "已追踪" else "追踪TA"

        text_content.text =
            "${Html.fromHtml(it.content)}${Html.fromHtml(it.content)}${Html.fromHtml(it.content)}${
                Html.fromHtml(it.content)
            }${Html.fromHtml(it.content)}${Html.fromHtml(it.content)}${Html.fromHtml(it.content)}${
                Html.fromHtml(
                    it.content
                )
            }${Html.fromHtml(it.content)}${Html.fromHtml(it.content)}${Html.fromHtml(it.content)}"
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

    private fun cancelFavorite() {
        showLoadingAlert("尝试取消...")

        TbsApi.boss().obtainCancelFollowBoss(entity.bossVO.id)
            .bindDefaultSub(doNext = {
                eventBus.post(RefreshUserEvent())

                entity.bossVO.isCollect = false
                layout_content.isSelected = false
                image_add.isVisible = true
                text_follow.text = "追踪TA"

                CancelFollowDialog.showDialog(supportFragmentManager, "cancelFollowBoss")
            }, doFail = {
                Toasts.show("取消失败，${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    private fun doFavorite() {
        showLoadingAlert("尝试追踪...")

        TbsApi.boss().obtainFollowBoss(entity.bossVO.id)
            .bindDefaultSub(doNext = {
                eventBus.post(RefreshUserEvent())

                entity.bossVO.isCollect = true
                layout_content.isSelected = true
                image_add.isVisible = false
                text_follow.text = "已追踪"

                SuccessFollowDialog.showDialog(supportFragmentManager, "successFollowBoss")
                    .apply {
                        onConfirmClick = SuccessFollowDialog.OnConfirmClick {
                            Toasts.show("开启推送")
                            this.dismiss()
                        }
                    }
            }, doFail = {
                Toasts.show("追踪失败，${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }
}