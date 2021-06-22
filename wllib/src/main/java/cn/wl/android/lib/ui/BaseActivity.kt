package cn.wl.android.lib.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import androidx.annotation.ColorRes
import cn.wl.android.lib.R
import cn.wl.android.lib.core.ErrorBean
import cn.wl.android.lib.data.core.DefResult
import cn.wl.android.lib.miss.EmptyMiss
import cn.wl.android.lib.miss.NetMiss
import cn.wl.android.lib.ui.common.LoadingDialog
import cn.wl.android.lib.ui.holder.HolderProxy
import cn.wl.android.lib.ui.holder.StatusCodePool
import cn.wl.android.lib.ui.holder.TitleProxy
import cn.wl.android.lib.utils.Toasts
import cn.wl.android.lib.view.holder.BaseHolder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 16:02 2020/9/23
 * @desc 基础界面
 */
abstract class BaseActivity : BaseCommonActivity() {

    private var mAlert: LoadingDialog? = null
    private var mTitleProxy: TitleProxy? = null
    private var mHolderProxy: HolderProxy? = null

    protected val titleBar: TitleProxy
        protected get() {
            if (mTitleProxy == null) {
                mTitleProxy = TitleProxy()
                mTitleProxy!!.initView(mActivity)
            }
            return mTitleProxy!!
        }

    override fun internalViewCreated() {
        super.internalViewCreated()
        val view = window.decorView

        mHolderProxy = object : HolderProxy(view) {
            override fun onCustomClick(statusCode: Int) {
                retryLoadData()
            }

            override fun onCustomHolder(statusCode: Int, data: Bundle) {
                if (statusCode == StatusCodePool.NONET_CODE || statusCode == StatusCodePool.TOP_NET_CODE) {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    dispatchStatusEvent(statusCode, data)
                }
            }

            override fun createCustomHolder(parent: ViewGroup, status: Int): BaseHolder? {
                return getCustomHolder(parent, status)
            }
        }
    }

    protected fun dispatchStatusEvent(statusCode: Int, data: Bundle?) {}

    protected fun getCustomHolder(parent: ViewGroup?, status: Int): BaseHolder? {
        return null
    }

    override fun showLoadingAlert(msg: String) {
        if (mAlert == null) {
            mAlert = LoadingDialog(mActivity)
        }
        mAlert!!.setMsg(msg)
        mAlert!!.show()

        val window = mAlert!!.window
        val dm = DisplayMetrics()
        mActivity?.windowManager?.defaultDisplay?.getMetrics(dm)
        val width = dm.widthPixels * 0.4
        val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        val params = window!!.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params
        window.setLayout(width.toInt(), height)
        window.attributes = params
        window.setLayout(width.toInt(), height)
        window.setBackgroundDrawableResource(R.drawable.draw_dialog_loading)
        mAlert!!.setCanceledOnTouchOutside(false)
        mAlert!!.setCancelable(false)
    }

    override fun hideLoadingAlert() {
        if (mAlert != null) {
            mAlert!!.dismiss()
        }
    }

    override fun showLoading() {
        mHolderProxy?.showLoading()
    }

    override fun showContent() {
        mHolderProxy?.showContent()
    }

    override fun showNetMiss(bean: ErrorBean) {
        mHolderProxy?.showNetMiss(bean)
    }

    override fun showDataFail(bean: ErrorBean) {
        mHolderProxy?.showDataFail(bean)
    }

    override fun showEmptyData(bean: ErrorBean) {
        mHolderProxy?.showEmptyData(bean)
    }

    override fun showViewByStatus(status: Int) {
        mHolderProxy?.showViewByStatus(status)
    }

    protected fun setHolderBackgroundColor(@ColorRes colorRes: Int) {
        mHolderProxy?.setBackgroundColor(colorRes)
    }

    override fun dispatchDataMiss(error: ErrorBean) {
        if (!onInterceptDataMiss(error)) {
            when (error.error) {
                is EmptyMiss -> showEmptyData(error)
                is NetMiss -> showNetMiss(error)
                else -> showDataFail(error)
            }
            if (isShowAlertToast()) {
                Toasts.show(error.msg)
            }
        }
    }

    /**
     * 是否显示提示
     * @return Boolean
     */
    protected open fun isShowAlertToast(): Boolean = true

    override fun onInterceptDataMiss(error: ErrorBean): Boolean {
        return false
    }

    /**
     * 注册RxJava回调
     *
     * 1. 已绑定界面, 会在界面销毁时自动回收
     * 2. 所有回调已同步到主线程执行
     *
     * @receiver Observable<T>
     * @param tryShowContent Boolean
     * @param doFail Function1<[@kotlin.ParameterName] ErrorBean, Unit>?
     * @param doDone Function1<[@kotlin.ParameterName] Boolean, Unit>?
     * @param doNext Function1<[@kotlin.ParameterName] T, Unit>
     */
    fun <T> Observable<T>.bindSubscribe(
        tryShowContent: Boolean = true,
        doFail: ((fail: ErrorBean) -> Unit)? = null,
        doDone: ((last: Boolean) -> Unit)? = null,
        doNext: ((data: T) -> Unit)
    ) {
        this.compose(bindDestroy())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DefResult<T>() {
                override fun doNext(data: T) {
                    doNext.invoke(data)

                    if (tryShowContent) {
                        showContent()
                    }
                }

                override fun doError(bean: ErrorBean) =
                    doFail?.invoke(bean) ?: dispatchDataMiss(bean)

                override fun doFinally(fromMiss: Boolean) =
                    doDone?.invoke(fromMiss) ?: hideLoadingAlert()
            })
    }

    /**
     * 注册RxJava回调
     *
     * 1. 已绑定界面, 会在界面销毁时自动回收
     * 2. 所有回调已同步到主线程执行
     *
     * @receiver Observable<Boolean>
     * @param alertMsg String   成功的提示信息
     * @param doFail Function1<[@kotlin.ParameterName] ErrorBean, Unit>?
     * @param doLast Function1<[@kotlin.ParameterName] Boolean, Unit>?
     * @param doSuccess Function1<[@kotlin.ParameterName] Boolean, Unit>
     */
    fun Observable<Boolean>.bindToastSub(
        alertMsg: String,
        doFail: ((fail: ErrorBean) -> Unit)? = null,
        doLast: ((last: Boolean) -> Unit)? = null,
        doSuccess: ((data: Boolean) -> Unit)
    ) {
        this.compose(bindDestroy())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DefResult<Boolean>() {
                override fun doNext(data: Boolean) {
                    Toasts.show(alertMsg)
                    doSuccess.invoke(data)
                }

                override fun doError(bean: ErrorBean) =
                    doFail?.invoke(bean) ?: super.doError(bean)

                override fun doFinally(fromMiss: Boolean) =
                    doLast?.invoke(fromMiss) ?: hideLoadingAlert()
            })
    }

    /**
     * 注册RxJava回调
     *
     * 1. 已绑定界面, 会在界面销毁时自动回收
     * 2. 所有回调已同步到主线程执行
     *
     * @receiver Observable<T>
     * @param doFail Function1<[@kotlin.ParameterName] ErrorBean, Unit>?
     * @param doLast Function1<[@kotlin.ParameterName] Boolean, Unit>?
     * @param doNext Function1<[@kotlin.ParameterName] T, Unit>
     */
    fun <T> Observable<T>.bindDefaultSub(
        doFail: ((fail: ErrorBean) -> Unit)? = null,
        doDone: ((last: Boolean) -> Unit)? = null,
        doNext: ((data: T) -> Unit)
    ): Disposable {
        val value = object : DefResult<T>() {
            override fun doNext(data: T) = doNext.invoke(data)
            override fun doError(bean: ErrorBean) =
                doFail?.invoke(bean) ?: Toasts.show(bean.msg)

            override fun doFinally(fromMiss: Boolean) =
                doDone?.invoke(fromMiss) ?: hideLoadingAlert()
        }
        this.compose(bindDestroy())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(value)

        return value
    }

}