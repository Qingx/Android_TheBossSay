package net.cd1369.tbs.android.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.greendao.gen.ArticleSimpleModelDao;
import com.greendao.gen.DaoMaster;

/**
 * Created by Xiang on 2021/8/30 15:25
 *
 * @description
 * @email Cymbidium@outlook.com
 */
public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        // 迁移数据库(如果修改了多个实体类，则需要把对应的Dao都传进来)
        MigrationHelper.migrate(db, ArticleSimpleModelDao.class);
    }
}
