package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.data.core.HttpConfig
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.utils.Toasts
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_mine.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.MineItem
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.event.JumpBossEvent
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.adapter.MineItemAdapter
import net.cd1369.tbs.android.ui.dialog.ServicePrivacyDialog
import net.cd1369.tbs.android.ui.start.InputPhoneActivity
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.jumpSysShare
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class MineFragment : BaseFragment() {
    private lateinit var mAdapter: MineItemAdapter

    companion object {
        fun createFragment(): MineFragment {
            return MineFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_mine
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        mAdapter = object : MineItemAdapter() {
            override fun onItemClick(item: MineItem) {
                when (item) {
                    MineItem.Favorite -> onClickFavorite()
                    MineItem.History -> onClickHistory()
                    MineItem.Contact -> ContactActivity.start(mActivity)
                    MineItem.Clear -> onClickClear()
                    else -> Toasts.show(item.itemName)
                }
            }
        }

        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        rv_content.adapter = mAdapter
        mAdapter.setNewData(MineItem.values().toMutableList())

        mAdapter.onRefreshLogin()

        image_info doClick {
            onClickInfo()
        }

        image_setting doClick {
            onClickInfo()
        }

        layout_history doClick {
            TodayHistoryActivity.start(mActivity)
        }

        layout_favorite doClick {
            onClickFavorite()
        }

        layout_follow doClick {
            eventBus.post(JumpBossEvent())
        }

        image_share doClick {
            jumpSysShare(mActivity, "https://www.bilibili.com/")
        }
    }

    //点击用户信息
    private fun onClickInfo() {
        if (UserConfig.get().loginStatus) {
            UserInfoActivity.start(mActivity)
        } else {
            Toasts.show("请先登录！")
            InputPhoneActivity.start(mActivity)
        }
    }

    //点击我的收藏
    private fun onClickFavorite() {
        if (UserConfig.get().loginStatus) {
            FavoriteActivity.start(mActivity)
        } else {
            Toasts.show("请先登录！")
            InputPhoneActivity.start(mActivity)
        }
    }

    //点击历史记录
    private fun onClickHistory() {
        if (UserConfig.get().loginStatus) {
            HistoryActivity.start(mActivity)
        } else {
            Toasts.show("请先登录！")
            InputPhoneActivity.start(mActivity)
        }
    }

    private fun onClickClear() {
        showLoadingAlert("正在清除缓存...")

        Observable.just(true)
            .observeOn(AndroidSchedulers.mainThread())
            .delay(1500, TimeUnit.MILLISECONDS)
            .bindDefaultSub {
                Toasts.show("清除成功")
                hideLoadingAlert()
            }
    }

    @SuppressLint("SetTextI18n")
    private fun setUserInfo() {
        val loginStatus = UserConfig.get().loginStatus
        val entity = UserConfig.get().userEntity

        if (loginStatus) {
            text_name.text = entity.nickName
            text_id.text = "ID：${entity.id}"
        } else {
            val tempId = DataConfig.get().tempId

            text_name.text = "请先登录！"
            text_id.text = "游客：${tempId.substring(0, 12)}..."
        }

        text_follow_num.text = entity.traceNum.toString()
        text_favorite_num.text = entity.collectNum.toString()
        text_history_num.text = entity.readNum.toString()

        mAdapter.onRefreshLogin()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: RefreshUserEvent) {
        loadData()
    }

    override fun loadData() {
        super.loadData()
        TbsApi.user().obtainRefreshUser().bindDefaultSub {
            HttpConfig.saveToken(it.token)

            UserConfig.get().userEntity = it.userInfo

            setUserInfo()
        }
    }
}