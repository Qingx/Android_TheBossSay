package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.data.core.HttpConfig
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.utils.Toasts
import com.advance.AdvanceBanner
import com.advance.AdvanceBannerListener
import com.advance.model.AdvanceError
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.android.synthetic.main.layout_ad.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.*
import net.cd1369.tbs.android.event.JumpBossEvent
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.adapter.MineItemAdapter
import net.cd1369.tbs.android.ui.dialog.ShareDialog
import net.cd1369.tbs.android.ui.start.InputPhoneActivity
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.doShareSession
import net.cd1369.tbs.android.util.doShareTimeline
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class MineFragment : BaseFragment(), AdvanceBannerListener {
    private var footer: View? = null
    private lateinit var mAdapter: MineItemAdapter
    private var advanceBanner: AdvanceBanner? = null

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
                    MineItem.Share -> onShare()
                    MineItem.About -> AboutUsActivity.start(mActivity)
                    MineItem.Contact -> ContactActivity.start(mActivity)
//                    MineItem.Clear -> onClickClear()
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
            onShare()
        }

        footer = LayoutInflater.from(mActivity).inflate(R.layout.layout_ad, null, false)
        mAdapter.addFooterView(footer)

        //rl是banner的父布局，用来展示广告
        //rl是banner的父布局，用来展示广告
        advanceBanner = AdvanceBanner(mActivity, footer!!.ll_ad, Const.BANNER_ID)
        //推荐：核心事件监听回调
        //推荐：核心事件监听回调
        advanceBanner?.setAdListener(this)
        //必须：请求策略并请求和展示广告
        //必须：请求策略并请求和展示广告
        advanceBanner?.loadStrategy()
    }

    private fun onShare() {
        ShareDialog.showDialog(requireFragmentManager(), "shareDialog")
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

    override fun onAdFailed(p0: AdvanceError?) {
        Log.e("OkHttp", "add...onAdFailed")
    }

    override fun onSdkSelected(p0: String?) {
//        Log.e("OkHttp", "add...onAdFailed")
    }

    override fun onAdShow() {
        Log.e("OkHttp", "add...onAdShow")
    }

    override fun onAdClicked() {

    }

    override fun onDislike() {

    }

    override fun onAdLoaded() {

    }
}