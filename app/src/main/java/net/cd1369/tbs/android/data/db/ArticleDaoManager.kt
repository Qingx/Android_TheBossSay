package net.cd1369.tbs.android.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.greendao.gen.ArticleSimpleModelDao
import com.greendao.gen.DaoMaster
import com.greendao.gen.DaoSession
import net.cd1369.tbs.android.data.model.ArticleSimpleModel

/**
 * Created by Xiang on 2021/8/26 14:32
 * @description
 * @email Cymbidium@outlook.com
 */
class ArticleDaoManager(val context: Context) {
    private var helper: DaoMaster.DevOpenHelper? = null
    private var daoMaster: DaoMaster? = null
    private var daoSession: DaoSession? = null
    private var articleDao: ArticleSimpleModelDao? = null

    companion object {
        private var mIns: ArticleDaoManager? = null

        fun getInstance(context: Context?): ArticleDaoManager {
            if (mIns == null) {
                synchronized(ArticleDaoManager::class.java) {
                    if (mIns == null) {
                        mIns = ArticleDaoManager(context!!)
                    }
                }
            }
            return mIns!!
        }
    }

    init {
        helper = DaoMaster.DevOpenHelper(context, "article_db", null)
        daoMaster = DaoMaster(getWritableDatabase())
        daoSession = daoMaster!!.newSession()
        articleDao = daoSession!!.articleSimpleModelDao
    }

    private fun getWritableDatabase(): SQLiteDatabase? {
        if (helper == null) {
            helper = DaoMaster.DevOpenHelper(context, "article_db", null)
        }
        return helper!!.writableDatabase
    }

    fun insertList(list: MutableList<ArticleSimpleModel>) {
        articleDao!!.insertOrReplaceInTx(list)
    }


    fun findAll(): MutableList<ArticleSimpleModel> {
        return articleDao!!.queryBuilder().build().list()
    }

    fun deleteAll() {
        val list = findAll()
        articleDao!!.deleteInTx(list)
    }
}