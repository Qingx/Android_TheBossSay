package net.cd1369.tbs.android.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.greendao.gen.DaoMaster;
import com.greendao.gen.DaoSession;
import com.greendao.gen.LabelModelDao;

import net.cd1369.tbs.android.data.model.LabelModel;

import java.util.List;

/**
 * Created by Xiang on 2021/8/26 11:08
 *
 * @description
 * @email Cymbidium@outlook.com
 */
public class LabelDaoManager {
    private DaoMaster.DevOpenHelper helper;
    private Context context;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private LabelModelDao billDao;

    private static LabelDaoManager billDaoManager;

    public static LabelDaoManager getInstance(Context context) {
        if (billDaoManager == null) {
            synchronized (LabelDaoManager.class) {
                if (billDaoManager == null) {
                    billDaoManager = new LabelDaoManager(context);
                }
            }
        }
        return billDaoManager;
    }

    private LabelDaoManager(Context context) {
        this.context = context;

        helper = new DaoMaster.DevOpenHelper(context, "bill_db", null);
        daoMaster = new DaoMaster(getWritableDatabase());
        daoSession = daoMaster.newSession();
        billDao = daoSession.getLabelModelDao();
    }

    private SQLiteDatabase getWritableDatabase() {
        if (helper == null) {
            helper = new DaoMaster.DevOpenHelper(context, "bill_db", null);
        }
        return helper.getWritableDatabase();
    }

    public void insertBill(List<LabelModel> models) {
        billDao.insertInTx(models);
    }
}
