package net.cd1369.tbs.android.data.cache

import cn.wl.android.lib.core.Page
import net.cd1369.tbs.android.data.model.ArticleSimpleModel
import net.cd1369.tbs.android.data.model.BossSimpleModel
import net.cd1369.tbs.android.data.model.LabelModel

/**
 * Created by Xiang on 2021/10/14 9:40
 * @description
 * @email Cymbidium@outlook.com
 */
object CacheConfig {
    private var mLabel: MutableList<LabelModel> = mutableListOf()
    private var mBoss: MutableList<BossSimpleModel> = mutableListOf()
    private var mArticle: Page<ArticleSimpleModel> = Page.empty()

    fun insertLabelList(list: MutableList<LabelModel>) {
        list.add(0, LabelModel.empty)
        mLabel = list
    }

    fun getAllLabel(): MutableList<LabelModel> {
        if (mLabel.isNullOrEmpty()) {
            mLabel = mutableListOf()
        }

        return mLabel
    }

    fun clearLabel() {
        mLabel = mutableListOf()
    }

    fun insertBossList(list: MutableList<BossSimpleModel>) {
        mBoss = list
    }

    fun getAllBoss(): MutableList<BossSimpleModel> {
        if (mBoss.isNullOrEmpty()) {
            mBoss = mutableListOf()
        }
        return mBoss
    }

    fun getBossByLabel(label: String): MutableList<BossSimpleModel> {
        var list = getAllBoss()

        if (!list.isNullOrEmpty() && label != "-1") {
            list = list.filter {
                it.labels.contains(label)
            }.toMutableList()
        }

        list.sort()
        return list
    }

    fun getBossWithLastByLabel(label: String): MutableList<BossSimpleModel> {
        var list = getBossByLabel(label)

        if (!list.isNullOrEmpty()) {
            list = list.filter {
                it.isLatest
            }.toMutableList()
        }

        list.sort()
        return list
    }

    fun updateBoss(entity: BossSimpleModel) {
        if (!mBoss.isNullOrEmpty()) {
            val index = mBoss.indexOfFirst {
                it.id == entity.id
            }

            if (index != -1) {
                mBoss[index] = entity
            }
        }
    }

    fun insertBoss(entity: BossSimpleModel) {
        if (mBoss.isNullOrEmpty()) {
            mBoss = mutableListOf()
        }
        mBoss.add(entity)
    }

    fun insertBatchBoss(list: MutableList<BossSimpleModel>) {
        if (mBoss.isNullOrEmpty()) {
            mBoss = mutableListOf()
        }

        mBoss.addAll(list)
    }

    fun deleteBoss(id: String) {
        if (mBoss.isNullOrEmpty()) {
            mBoss = mutableListOf()
        }

        var index = mBoss.indexOfFirst {
            it.id == id
        }

        if (index != -1) {
            mBoss.removeAt(index)
        }
    }

    fun clearBoss() {
        mBoss = mutableListOf()
    }

    fun insertArticle(page: Page<ArticleSimpleModel>) {
        mArticle = page
    }

    fun getAllArticle(): Page<ArticleSimpleModel> {
        if (mArticle == null) {
            mArticle = Page.empty()
        }

        return mArticle
    }

    fun clearArticle() {
        mArticle = Page.empty()
    }

    fun clearAll() {
        clearLabel()
        clearBoss()
        clearArticle()
    }
}