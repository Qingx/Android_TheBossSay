package net.cd1369.tbs.android.util

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import cn.jiguang.verifysdk.api.JVerificationInterface
import cn.jiguang.verifysdk.api.JVerifyUIClickCallback
import cn.jiguang.verifysdk.api.JVerifyUIConfig
import cn.jpush.android.api.JPushInterface
import cn.wl.android.lib.config.WLConfig
import cn.wl.android.lib.utils.Toasts
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import net.cd1369.tbs.android.BuildConfig
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.Const.SERVICE_URL
import net.cd1369.tbs.android.config.TbsApp
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.ui.home.LoginPhoneWechatActivity
import net.cd1369.tbs.android.util.Tools.logE
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object JPushHelper {

    private var mAddTag: Disposable? = null
    private var mAddTags: Disposable? = null
    private var mAddAlias: Disposable? = null
    private val mVersion = AtomicInteger(0)
    private val mContext get() = WLConfig.getContext()

    /**
     * 尝试启动推送
     */
    fun tryStartPush() {
        JPushInterface.resumePush(TbsApp.mContext)

        val user = UserConfig.get().userEntity

        val userId = user.id
        if (user.type != "0") {
            tryAddAlias(userId)
        }
        tryAddTags(user.tags ?: mutableListOf())
    }

    /**
     * 尝试注册别名
     * @param userId String
     */
    fun tryAddAlias(userId: String) {
        mAddAlias?.dispose()

        JPushInterface.setAlias(
            mContext,
            userId
        ) { p0, p1, p2 ->
            "设置极光alias: $p0 ${p1 ?: "null"} $p2".logE()

            if (p0 == 6002) {
                mAddAlias = Observable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tryAddAlias(userId)
                    }) {

                    }
            }
        }
    }

    /**
     * 尝试添加tag
     * @param tag String
     */
    fun tryAddTag(tag: String) {
        mAddTag?.dispose()

        JPushInterface.setTags(
            mContext,
            setOf(tag)
        ) { p0, p1, p2 ->
            "设置极光tag: $p0 ${p1 ?: "null"} $p2".logE()

            if (p0 == 6002) {
                mAddTag = Observable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tryAddTag(tag)
                    }) {

                    }
            }
        }
    }

    /**
     * 尝试添加tag
     * @param tags MutableList<String>
     */
    fun tryAddTags(tags: MutableList<String>) {
        if (tags.isNullOrEmpty()) return

        mAddTags?.dispose()

//        val testTag = mutableListOf("0123456789")

        JPushInterface.setTags(
            mContext, tags.toSet()
        ) { p0, p1, p2 ->
            "设置极光tag: $p0 ${p1 ?: "null"} $p2".logE()

            if (p0 == 6002) {
                mAddTags = Observable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tryAddTags(tags)
                    }) {

                    }
            }
        }
    }

    /**
     * 尝试删除tag
     * @param tag String
     */
    fun tryDelTag(tag: String) {
        try {
            JPushInterface.deleteTags(
                mContext,
                mVersion.getAndIncrement(),
                setOf(tag)
            )
        } catch (e: Exception) {
        }
    }


    /**
     * 尝试重置推送
     */
    fun tryClearTagAlias() {
        JPushInterface.cleanTags(mContext, mVersion.getAndIncrement())
    }

    fun jumpLogin(context: Activity, doLogin: () -> Unit) {
        val result = JVerificationInterface.checkVerifyEnable(context)
        result.logE(prefix = "极光认证检测result")
        if (result) {
            val width = ScreenUtils.getAppScreenWidth()
            val privacyUrl =
                if (BuildConfig.ENV != "MI") Const.PRIVACY_URL else Const.MI_PRIVACY_URL
            val privacyBack = LayoutInflater.from(context)
                .inflate(R.layout.layout_return_back, null, false)
            val title =
                LayoutInflater.from(context).inflate(R.layout.layout_jverify_title, null, false)
            val other =
                LayoutInflater.from(context).inflate(R.layout.layout_jverify_other, null, false)

            val uiConfig = JVerifyUIConfig.Builder()
                .setStatusBarDarkMode(true)
                .setStatusBarColorWithNav(true)
                .setNavColor(ColorUtils.getColor(R.color.colorWhite))
                .setNavReturnImgPath("ic_back_dark")
                .setNavReturnBtnHeight(20)
                .setNavReturnBtnWidth(20)
                .setNavReturnBtnOffsetX(16)
                .setNumberColor(ColorUtils.getColor(R.color.colorTextDark))
                .setNumberSize(20)
                .setLogoHidden(true)
                .setLogBtnImgPath("draw_accent_4")
                .setLogBtnText("一键登录")
                .setLogBtnTextSize(16)
                .setLogBtnHeight(44)
                .setLogBtnWidth(ConvertUtils.px2dp(width.toFloat()) - 32)
                .setUncheckedImgPath("ic_checkbox_normal")
                .setCheckedImgPath("ic_checkbox_select")
                .setPrivacyCheckboxSize(10)
                .setAppPrivacyOne("服务条款", SERVICE_URL)
                .setAppPrivacyTwo("隐私协议", privacyUrl)
                .setAppPrivacyColor(
                    ColorUtils.getColor(R.color.colorTextGray),
                    ColorUtils.getColor(R.color.colorAccent)
                )
                .setPrivacyText("我已阅读并同意", "和", "、", "")
                .setAppPrivacyNavTitle1("服务条款")
                .setAppPrivacyNavTitle2("隐私协议")
                .setPrivacyNavReturnBtn(privacyBack)
                .setPrivacyNavColor(ColorUtils.getColor(R.color.colorWhite))
                .setPrivacyNavTitleTextColor(ColorUtils.getColor(R.color.colorTextDark))
                .setPrivacyStatusBarColorWithNav(true)
                .setPrivacyStatusBarDarkMode(true)
                .addCustomView(title, false)
                { context, view ->
                    Toasts.show("点击title")
                }
                .addCustomView(other, true)
                { context, view ->
                    LoginPhoneWechatActivity.start(context)
                }
                .build()

            JVerificationInterface.setCustomUIWithConfig(uiConfig)

            JVerificationInterface.loginAuth(
                context,
            ) { code, content, operator ->
                code.logE(prefix = "极光认证登录code")
                content.logE(prefix = "极光认证登录content")
                operator.logE(prefix = "极光认证登录operator")

                when (code.toString()) {
                    "6000" -> doLogin.invoke()
                    "6002" -> "取消授权".logE(prefix = "")
                    else -> Toasts.show(content)
                }
            }
        } else {
            LoginPhoneWechatActivity.start(context)
        }
    }
}