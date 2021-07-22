package net.cd1369.tbs.android.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.greendao.gen.BossLabelEntityDao;
import com.greendao.gen.DaoMaster;
import com.greendao.gen.DaoSession;

import net.cd1369.tbs.android.config.TbsApp;
import net.cd1369.tbs.android.data.entity.BossLabelEntity;

import java.util.List;

/**
 * Created by Xiang on 2021/7/22 14:10
 *
 * @description
 * @email Cymbidium@outlook.com
 */
public class BossLabelDaoManager {
    private DaoMaster.DevOpenHelper helper;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private BossLabelEntityDao labelDao;

    private static BossLabelDaoManager ins;

    public static BossLabelDaoManager getInstance() {
        if (ins == null) {
            synchronized (BossLabelDaoManager.class) {
                if (ins == null) {
                    ins = new BossLabelDaoManager();
                }
            }
        }
        return ins;
    }

    private BossLabelDaoManager() {

        helper = new DaoMaster.DevOpenHelper(TbsApp.getContext(), "boss_label_db", null);
        daoMaster = new DaoMaster(getWritableDatabase());
        daoSession = daoMaster.newSession();
        labelDao = daoSession.getBossLabelEntityDao();
    }

    private SQLiteDatabase getWritableDatabase() {
        if (helper == null) {
            helper = new DaoMaster.DevOpenHelper(TbsApp.getContext(), "boss_label_db", null);
        }
        return helper.getWritableDatabase();
    }

    public void insertLabel(BossLabelEntity entity) {
        labelDao.insert(entity);
    }

    public void insertList(List<BossLabelEntity> list) {
        list.forEach(e -> {
            labelDao.insertOrReplace(e);
        });
    }

    public List<BossLabelEntity> getAllLabel() {
        return labelDao.loadAll();
    }

}
