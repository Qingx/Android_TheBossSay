package net.cd1369.tbs.android.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import cn.wl.android.lib.utils.Times
import com.greendao.gen.BossSimpleModelDao
import com.greendao.gen.DaoMaster
import com.greendao.gen.DaoMaster.DevOpenHelper
import com.greendao.gen.DaoSession
import net.cd1369.tbs.android.data.db.LabelDaoManager
import net.cd1369.tbs.android.data.model.BossSimpleModel
import java.util.*

/**
 * Created by Xiang on 2021/8/26 11:41
 *
 * @description
 * @email Cymbidium@outlook.com
 */
class BossDaoManager(val context: Context) {
    private var helper: MyOpenHelper? = null
    private var daoMaster: DaoMaster? = null
    private var daoSession: DaoSession? = null
    private var bossDao: BossSimpleModelDao? = null

    companion object {
        private var bossDaoManager: BossDaoManager? = null

        fun getInstance(context: Context): BossDaoManager {
            if (bossDaoManager == null) {
                synchronized(LabelDaoManager::class.java) {
                    if (bossDaoManager == null) {
                        bossDaoManager = BossDaoManager(context)
                    }
                }
            }
            return bossDaoManager!!
        }
    }

    init {
        helper = MyOpenHelper(context, "boss_db", null)
        daoMaster = DaoMaster(getWritableDatabase())
        daoSession = daoMaster!!.newSession()
        bossDao = daoSession!!.bossSimpleModelDao
    }

    private fun getWritableDatabase(): SQLiteDatabase? {
        if (helper == null) {
            helper = MyOpenHelper(context, "boss_db", null)
        }
        return helper!!.writableDatabase
    }

    fun insert(model: BossSimpleModel) {
        bossDao!!.insertOrReplace(model)
    }

    fun insertList(models: MutableList<BossSimpleModel>) {
        bossDao!!.insertOrReplaceInTx(models)
    }

    fun update(model: BossSimpleModel) {
        bossDao!!.update(model)
    }

    fun findAll(): MutableList<BossSimpleModel> {
        val list = bossDao!!.queryBuilder().build().list()
        return list
    }

    fun delete(id: Long) {
        val model =
            bossDao!!.queryBuilder().where(BossSimpleModelDao.Properties.Id.eq(id)).build().unique()
        bossDao!!.delete(model)
    }

    fun deleteAll() {
        val list = findAll()
        bossDao!!.deleteInTx(list)
    }

    fun findByLabel(label: String): MutableList<BossSimpleModel> {
        val allList = findAll()
        var list = mutableListOf<BossSimpleModel>()
        list = if (label == "-1") {
            allList
        } else {
            allList.filter {
                it.labels.contains(label)
            }.toMutableList()
        }
        list.sort()
        return list
    }

    fun findLatest(label: String): MutableList<BossSimpleModel> {
        val allList = findAll()
        var list = mutableListOf<BossSimpleModel>()
        list = if (label == "-1") {
            allList
        } else {
            allList.filter {
                it.labels.contains(label) && it.isLatest
            }.toMutableList()
        }
        list.sort()
        return list
    }
}