package net.cd1369.tbs.android.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.greendao.gen.DaoMaster
import com.greendao.gen.DaoMaster.DevOpenHelper
import com.greendao.gen.DaoSession
import com.greendao.gen.LabelModelDao
import net.cd1369.tbs.android.data.model.LabelModel

/**
 * Created by Xiang on 2021/8/26 13:39
 * @description
 * @email Cymbidium@outlook.com
 */
class LabelDaoManager(val context: Context) {
    private var helper: MyOpenHelper? = null
    private var daoMaster: DaoMaster? = null
    private var daoSession: DaoSession? = null
    private var labelDao: LabelModelDao? = null

    companion object {
        private var mIns: LabelDaoManager? = null

        fun getInstance(context: Context?): LabelDaoManager {
            if (mIns == null) {
                synchronized(LabelDaoManager::class.java) {
                    if (mIns == null) {
                        mIns = LabelDaoManager(context!!)
                    }
                }
            }
            return mIns!!
        }
    }

    init {
        helper = MyOpenHelper(context, "label_db", null)
        daoMaster = DaoMaster(getWritableDatabase())
        daoSession = daoMaster!!.newSession()
        labelDao = daoSession!!.labelModelDao
    }

    private fun getWritableDatabase(): SQLiteDatabase? {
        if (helper == null) {
            helper = MyOpenHelper(context, "label_db", null)
        }
        return helper!!.writableDatabase
    }

    fun insert(label: LabelModel) {
        labelDao!!.insertOrReplace(label)
    }

    fun insertList(list: MutableList<LabelModel>) {
        labelDao!!.insertOrReplaceInTx(list)
    }

    fun findAll(): MutableList<LabelModel> {
        return labelDao!!.queryBuilder().build().list()
    }
}