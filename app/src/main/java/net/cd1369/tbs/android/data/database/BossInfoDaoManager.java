package net.cd1369.tbs.android.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.greendao.gen.BossInfoEntityDao;
import com.greendao.gen.BossLabelEntityDao;
import com.greendao.gen.DaoMaster;
import com.greendao.gen.DaoSession;

import net.cd1369.tbs.android.config.TbsApp;
import net.cd1369.tbs.android.data.entity.BossInfoEntity;
import net.cd1369.tbs.android.data.entity.BossLabelEntity;

import java.util.List;

/**
 * Created by Xiang on 2021/7/22 14:10
 *
 * @description
 * @email Cymbidium@outlook.com
 */
public class BossInfoDaoManager {

    private DaoMaster.DevOpenHelper helper;

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private BossInfoEntityDao infoDao;

    private static BossInfoDaoManager ins;

    public static BossInfoDaoManager getInstance() {
        if (ins == null) {
            synchronized (BossInfoDaoManager.class) {
                if (ins == null) {
                    ins = new BossInfoDaoManager();
                }
            }
        }
        return ins;
    }

    private BossInfoDaoManager() {
        helper = new DaoMaster.DevOpenHelper(TbsApp.getContext(), "boss_info_db", null);
        daoMaster = new DaoMaster(getWritableDatabase());
        daoSession = daoMaster.newSession();
        infoDao = daoSession.getBossInfoEntityDao();
    }

    private SQLiteDatabase getWritableDatabase() {
        if (helper == null) {
            helper = new DaoMaster.DevOpenHelper(TbsApp.getContext(), "boss_info_db", null);
        }
        return helper.getWritableDatabase();
    }

    public void insertLabel(BossInfoEntity entity) {
        infoDao.insertOrReplace(entity);
    }

    public void insertList(List<BossInfoEntity> list) {
        list.forEach(e -> {
            infoDao.insertOrReplace(e);
        });
    }

    public List<BossInfoEntity> getAllBoss() {
        return infoDao.loadAll();
    }

}
