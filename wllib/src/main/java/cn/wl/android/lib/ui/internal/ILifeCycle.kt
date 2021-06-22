package cn.wl.android.lib.ui.internal

import io.reactivex.ObservableTransformer

/**
 * Created by JustBlue on 2019-08-26.
 *
 * @email: bo.li@cdxzhi.com
 * @desc: 界面生命周期管理
 */
interface ILifeCycle {
    /** 对应 [Fragment.onDetach]  */
    /**
     * 将当前[io.reactivex.Observable]事件流绑定
     * 生命周期[.CYC_DESTROY], 自动注销
     *
     * @param <T>
     * @return
    </T> */
    fun <T> bindDestroy(): ObservableTransformer<T, T>?

    /**
     * 界面注销是调用
     * @param runDestroy
     */
    fun doOnDestroy(runDestroy: Runnable?)

    companion object {
        const val CYC_CREATE = 0

        /** [Activity.onCreate]  */
        const val CYC_START_ = 1

        /** [Activity.onStart]  */
        const val CYC_RESUME = 2

        /** [Activity.onResume]  */
        const val CYC_PASUE_ = 3

        /** [Activity.onPause]  */
        const val CYC_STOP_X = 4

        /** [Activity.onStop]  */
        const val CYC_DESTROY = 5

        /** [Activity.onDestroy]  */
        const val FYC_ATTACH = 6

        /** 对应 [Fragment.onAttach]  */
        const val FYC_DETTCH = 7
    }

}