package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseActivity
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_favorite.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.model.TestFavoriteEntity
import net.cd1369.tbs.android.ui.adapter.FavoriteAdapter
import net.cd1369.tbs.android.ui.dialog.AddFolderDialog
import net.cd1369.tbs.android.util.doClick
import java.util.concurrent.TimeUnit

class FavoriteActivity : BaseActivity() {
    private lateinit var mAdapter: FavoriteAdapter
    private var needLoading = true

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, FavoriteActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_favorite
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
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
            override fun onContentItemClick(contentItem: Int) {

            }

            override fun onItemDelete(parentItem: Int, onRemove: (item: Int) -> Unit) {
                onRemove.invoke(parentItem)
            }

            override fun onContentItemDelete(contentItem: Int, onRemove: (item: Int) -> Unit) {
                onRemove.invoke(contentItem)
            }
        }
        rv_content.adapter = mAdapter

        button_float doClick {
            AddFolderDialog.showDialog(supportFragmentManager).apply {
                this.onConfirmClick = AddFolderDialog.OnConfirmClick {
                    val item = TestFavoriteEntity(5, mutableListOf(0, 1, 2))
                    mAdapter.addFolder(item)
                    this.dismiss()
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

        val testData = mutableListOf(
            TestFavoriteEntity(0, mutableListOf(0, 1, 2)),
            TestFavoriteEntity(1, mutableListOf(0, 1)),
            TestFavoriteEntity(2, mutableListOf(0, 1, 2, 3)),
            TestFavoriteEntity(3, mutableListOf(0, 1, 2, 3, 4)),
            TestFavoriteEntity(4, mutableListOf(0, 1, 2, 3, 4, 5, 6)),
        )

        if (needLoading) showLoading()

        Observable.just(testData)
            .observeOn(AndroidSchedulers.mainThread())
            .delay(2, TimeUnit.SECONDS)
            .bindDefaultSub {
                showContent()
                layout_refresh.finishRefresh()

                mAdapter.setNewData(it)
                needLoading = true

                val list = it.flatMap { data ->
                    data.data
                }

                text_title.text = "我的收藏（${list.size})"
            }
    }
}