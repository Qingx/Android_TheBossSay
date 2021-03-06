package net.cd1369.tbs.android.ui.start

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.core.PageParam
import cn.wl.android.lib.data.core.HttpConfig
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Toasts
import kotlinx.android.synthetic.main.activity_guide.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.cache.CacheConfig
import net.cd1369.tbs.android.data.entity.TokenEntity
import net.cd1369.tbs.android.data.model.BossSimpleModel
import net.cd1369.tbs.android.ui.adapter.GuideInfoAdapter
import net.cd1369.tbs.android.ui.home.ArticleActivity
import net.cd1369.tbs.android.ui.home.HomeActivity
import net.cd1369.tbs.android.util.Tools.logE
import net.cd1369.tbs.android.util.doClick
import java.util.concurrent.TimeUnit

class GuideActivity : BaseActivity() {

    companion object {
        private lateinit var bossList: MutableList<BossSimpleModel>

        fun start(context: Context?, list: MutableList<BossSimpleModel>) {
            val intent = Intent(context, GuideActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    bossList = list
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_guide
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        text_time.text = "跳过"

        val adapter = object : GuideInfoAdapter() {
            override fun onAddFollow(data: MutableList<String>) {
                showLoadingAlert("搜寻并追踪...")

                TbsApi.boss().obtainGuideFollow(data)
                    .flatMap {

                        val tackList = bossList.filter {
                            data.contains(it.id)
                        }.toMutableList()

                        if (!tackList.isNullOrEmpty()) {
                            CacheConfig.insertBossList(tackList)
                        }

                        TbsApi.boss().obtainTackArticle("-1", PageParam.create(1, 10))
                    }
                    .onErrorReturn { Page.empty() }
                    .flatMap {
                        CacheConfig.insertArticle(it)

                        TbsApi.user().obtainRefreshUser()
                    }
                    .onErrorReturn {
                        TokenEntity(HttpConfig.getToken(), UserConfig.get().userEntity)
                    }
                    .timeout(8, TimeUnit.SECONDS)
                    .bindDefaultSub(doNext = {
                        Toasts.show("追踪成功")
                        hideLoadingAlert()

                        HttpConfig.saveToken(it.token)
                        UserConfig.get().userEntity = it.userInfo

                        DataConfig.get().firstUse = false

                        if (WelActivity.tempId.isNullOrEmpty()) {
                            HomeActivity.start(mActivity)
                            mActivity?.finish()
                        } else {
                            val intentHome = Intent(mActivity, HomeActivity::class.java)
                            intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                            val intentArticle = Intent(mActivity, ArticleActivity::class.java)
                            intentArticle.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intentArticle.putExtra("articleId", WelActivity.tempId)

                            mActivity.startActivities(arrayOf(intentHome, intentArticle))
                            mActivity?.finish()
                        }
                    }, doFail = {
                        hideLoadingAlert()
                        it.msg.logE()
                        Toasts.show(it.msg)
                    })
            }
        }

        rv_content.adapter = adapter
        rv_content.layoutManager =
            object : GridLayoutManager(mActivity, 2, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        adapter.setNewData(bossList)

        layout_add doClick {
            adapter.addFollow()
        }

        text_clear doClick {
            adapter.clearAll()
        }

        text_time doClick {
            val tempId = WelActivity.tempId

            DataConfig.get().firstUse = false

            if (tempId.isNullOrEmpty()) {
                showLoadingAlert("加载数据...")
                TbsApi.boss().obtainTackArticle("-1", PageParam.create(1, 10))
                    .onErrorReturn { Page.empty() }
                    .flatMap {
                        CacheConfig.insertBossList(mutableListOf())
                        CacheConfig.insertArticle(it)

                        TbsApi.user().obtainRefreshUser()
                    }.onErrorReturn {
                        TokenEntity(HttpConfig.getToken(), UserConfig.get().userEntity)
                    }
                    .timeout(8, TimeUnit.SECONDS)
                    .bindDefaultSub {
                        HttpConfig.saveToken(it.token)
                        UserConfig.get().userEntity = it.userInfo

                        HomeActivity.start(mActivity)
                        mActivity?.finish()
                    }

            } else {
                TbsApi.user().obtainRefreshUser()
                    .onErrorReturn {
                        TokenEntity(HttpConfig.getToken(), UserConfig.get().userEntity)
                    }.timeout(8, TimeUnit.SECONDS)
                    .bindDefaultSub {
                        val intentHome = Intent(mActivity, HomeActivity::class.java)
                        intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                        val intentArticle = Intent(mActivity, ArticleActivity::class.java)
                        intentArticle.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intentArticle.putExtra("articleId", tempId)

                        mActivity.startActivities(arrayOf(intentHome, intentArticle))
                    }

            }
        }
    }
}