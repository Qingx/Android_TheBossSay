package cn.wl.android.lib.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.wl.android.lib.R
import cn.wl.android.lib.core.ErrorBean
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.core.PageParam
import cn.wl.android.lib.data.core.DefResult
import cn.wl.android.lib.miss.EmptyMiss
import cn.wl.android.lib.miss.NetMiss
import cn.wl.android.lib.ui.common.LoadView
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter.RequestLoadMoreListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer

/**
 * Created by JustBlue on 2019-08-29.
 *
 * @email: bo.li@cdxzhi.com
 * @desc: 分页Activity基类
 */
abstract class BaseListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    RequestLoadMoreListener {

    private var mPageParam: PageParam? = null
    private var rvContent: RecyclerView? = null
    private var refLayout: SwipeRefreshLayout? = null
    private var mAdapter: BaseQuickAdapter<*, *>? = null

    /**
     * 获取[androidx.recyclerview.widget.RecyclerView.LayoutManager]
     *
     * @return
     */
    protected val layoutManager: RecyclerView.LayoutManager
        protected get() = LinearLayoutManager(mActivity)

    override fun internalViewCreated() {
        super.internalViewCreated()
        rvContent = findViewById(R.id.rv_content)
        refLayout = findViewById(R.id.refresh_layout)

        rvContent?.let { initRecyclerView(it) }
        refLayout?.let { initRefreshLayout(it) }
    }

    /**
     * 初始化刷新控件
     *
     * @param refLayout
     */
    protected open fun initRefreshLayout(refLayout: SwipeRefreshLayout) {
        refLayout.setOnRefreshListener(this)
    }

    /**
     * 初始化
     *
     * @param recyclerView
     */
    protected open fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    /**
     * 获取RecyclerView的Adapter
     *
     * @return
     */
    protected open val adapter: BaseQuickAdapter<*, *>?
        protected get() {
            if (mAdapter == null) {
                mAdapter = createAdapter()
                mAdapter!!.setLoadMoreView(LoadView { mAdapter!!.loadMoreComplete() })
                mAdapter!!.setOnLoadMoreListener(this)
            }
            return mAdapter
        }

    /**
     * 创建适配器
     *
     * @return
     */
    protected abstract fun createAdapter(): BaseQuickAdapter<*, *>?

    override fun onRefresh() {
        loadData()
    }

    override fun loadData() {
        loadData(false)
    }

    /**
     * 判断当前adapter是非为空
     *
     * @return
     */
    protected val isEmptyAdapter: Boolean
        protected get() {
            val adapter = mAdapter
            if (adapter != null) {
                val data = adapter.data
                return data.isEmpty()
            }
            return true
        }

    /**
     * 加载数据
     *
     * @param loadMore 是否为分页加载更多
     */
    protected open fun loadData(loadMore: Boolean) {
        if (!loadMore) {
            if (mPageParam != null) {
                mPageParam!!.resetPage()
            }
            if (isEmptyAdapter) {
                showLoading()
            }
        }
    }

    protected fun <T> Observable<List<T>>.bindListSubscribe(
        doFail: ((error: ErrorBean) -> Unit)? = null,
        doDone: ((fromMiss: Boolean) -> Unit)? = null,
        doNext: (data: List<T>) -> Unit,
    ) = bindListSub(this, doNext, doFail, doDone)

    /**
     * 绑定集合数据
     *
     * @param source
     * @param doNext
     * @param doFail
     * @param doDone
     * @param <T>
    </T> */
    protected fun <T> bindListSub(
        source: Observable<List<T>>,
        doNext: Consumer<List<T>>,
        doFail: Consumer<ErrorBean>? = null,
        doDone: Consumer<Boolean>? = null
    ) {
        source.compose(bindDestroy())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DefResult<List<T>>() {
                @Throws(Exception::class)
                protected override fun doNext(data: List<T>) {
                    showContent()
                    doNext.accept(data)
                    tryCompleteStatus(false)
                }

                @Throws(Exception::class)
                override fun doError(bean: ErrorBean) {
                    bean.isLoadMore = false
                    bean.mode = ErrorBean.MODE_LIST
                    if (doFail != null) {
                        doFail.accept(bean)
                    } else {
                        dispatchDataMiss(bean)
                    }
                }

                @Throws(Exception::class)
                override fun doFinally(fromMiss: Boolean) {
                    if (doDone != null) {
                        doDone.accept(fromMiss)
                    } else {
                        hideLoadingAlert()
                    }
                }
            })
    }

    protected fun <T> Observable<Page<T>>.bindPageSubscribe(
        loadMore: Boolean,
        doFail: ((error: ErrorBean) -> Unit)? = null,
        doDone: ((fromMiss: Boolean) -> Unit)? = null,
        doNext: (data: List<T>) -> Unit,
    ) = bindPageSub(loadMore, this, doNext, doFail, doDone)

    /**
     * 绑定分页数据
     *
     * @param loadMore
     * @param pageSource
     * @param doNext
     * @param doFail
     * @param doDone
     * @param <T>
    </T> */
    protected fun <T> bindPageSub(
        loadMore: Boolean,
        pageSource: Observable<Page<T>>,
        doNext: Consumer<List<T>>,
        doFail: Consumer<ErrorBean>? = null,
        doDone: Consumer<Boolean>? = null
    ) {
        pageSource.compose(bindDestroy())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DefResult<Page<T>>() {
                @Throws(Exception::class)
                override fun doNext(data: Page<T>) {
                    showContent()

                    // 驱动下一页数据
                    tryNextPageParam(data)

                    // 回传处理数据绑定
                    doNext.accept(data.records)
                    tryCompleteStatus(data.hasData())
                }

                @Throws(Exception::class)
                override fun doError(bean: ErrorBean) {
                    bean.isLoadMore = loadMore
                    bean.mode = ErrorBean.MODE_MORE
                    if (doFail != null) {
                        doFail.accept(bean)
                    } else {
                        if (isEmptyAdapter || !loadMore) {
                            tryFinishRefresh()
                            dispatchDataMiss(bean)
                        } else {
                            tryFailureStatus()
                        }
                    }
                }

                @Throws(Exception::class)
                override fun doFinally(fromMiss: Boolean) {
                    if (doDone != null) {
                        doDone.accept(fromMiss)
                    } else {
                        hideLoadingAlert()
                    }
                }
            })
    }

    /**
     * 获取当前分页参数
     *
     * @return
     */
    protected val pageParam: PageParam?
        protected get() {
            if (mPageParam == null) {
                mPageParam = createPageParam()
            }
            return mPageParam
        }

    /**
     * 创建默认的分页参数
     *
     * @return
     */
    protected fun createPageParam(): PageParam {
        return PageParam.create()
    }

    /**
     * 尝试更新分页数据, 并驱动分页数据指向下一页
     *
     * @param dataSource 当前分页实体类
     */
    private fun tryNextPageParam(dataSource: Page<*>) {
        val param = mPageParam
        param?.nextPage(dataSource.current)
    }

    override fun onInterceptDataMiss(error: ErrorBean): Boolean {
        val mode = error.mode
        if (mode == ErrorBean.MODE_LIST) {
            val throwable = error.error
            if (throwable is NetMiss) {
                if (!isEmptyAdapter) {
                    Toasts.show(error.msg)
                    return true
                }
            } else {
                mAdapter!!.setNewData(null)
            }
        } else if (mode == ErrorBean.MODE_MORE) {
            val loadMore = error.isLoadMore
            val throwable = error.error
            if (throwable is EmptyMiss) {
                if (!loadMore || isEmptyAdapter) {
                    mAdapter!!.setNewData(null)
                    showEmptyData(error)
                } else {
                    tryCompleteStatus(false)
                }
                return true
            } else if (throwable is NetMiss) {
                if (!isEmptyAdapter) {
                    Toasts.show(error.msg)
                    return true
                }
            } else {
                if (!isEmptyAdapter) {
                    tryFailureStatus()
                    return true
                }
            }
        }
        return super.onInterceptDataMiss(error)
    }

    /**
     * 尝试更新完成状态
     *
     * @param hasData
     */
    /**
     * 尝试更新完成状态
     *
     * @param hasData
     */
    protected fun tryCompleteStatus(hasData: Boolean, hideEnd: Boolean = false) {
        tryFinishRefresh()

        mAdapter!!.loadMoreComplete()
        if (!hasData) {
            mAdapter!!.loadMoreEnd(hideEnd)
        }
    }

    /**
     * 尝试设置失败状态
     */
    protected fun tryFailureStatus() {
        tryFinishRefresh()
        if (mAdapter!!.isLoading) {
            mAdapter!!.loadMoreFail()
        }
    }

    protected fun tryFinishRefresh() {
        if (refLayout != null && refLayout!!.isRefreshing) {
            refLayout!!.isRefreshing = false
        }
    }

    override fun onLoadMoreRequested() {
        loadData(true)
    }


}