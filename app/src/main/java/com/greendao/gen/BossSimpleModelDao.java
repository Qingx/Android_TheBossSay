package com.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import java.util.List;
import net.cd1369.tbs.android.util.StringConvert;

import net.cd1369.tbs.android.data.model.BossSimpleModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BOSS_SIMPLE_MODEL".
*/
public class BossSimpleModelDao extends AbstractDao<BossSimpleModel, Long> {

    public static final String TABLENAME = "BOSS_SIMPLE_MODEL";

    /**
     * Properties of entity BossSimpleModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Head = new Property(2, String.class, "head", false, "HEAD");
        public final static Property Role = new Property(3, String.class, "role", false, "ROLE");
        public final static Property Top = new Property(4, boolean.class, "top", false, "TOP");
        public final static Property UpdateTime = new Property(5, Long.class, "updateTime", false, "UPDATETIME");
        public final static Property Labels = new Property(6, String.class, "labels", false, "LABELS");
        public final static Property PhotoUrl = new Property(7, String.class, "photoUrl", false, "PHOTO_URL");
    }

    private final StringConvert labelsConverter = new StringConvert();
    private final StringConvert photoUrlConverter = new StringConvert();

    public BossSimpleModelDao(DaoConfig config) {
        super(config);
    }
    
    public BossSimpleModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BOSS_SIMPLE_MODEL\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"HEAD\" TEXT," + // 2: head
                "\"ROLE\" TEXT," + // 3: role
                "\"TOP\" INTEGER NOT NULL ," + // 4: top
                "\"UPDATETIME\" INTEGER," + // 5: updateTime
                "\"LABELS\" TEXT," + // 6: labels
                "\"PHOTO_URL\" TEXT);"); // 7: photoUrl
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BOSS_SIMPLE_MODEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, BossSimpleModel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String head = entity.getHead();
        if (head != null) {
            stmt.bindString(3, head);
        }
 
        String role = entity.getRole();
        if (role != null) {
            stmt.bindString(4, role);
        }
        stmt.bindLong(5, entity.getTop() ? 1L: 0L);
 
        Long updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(6, updateTime);
        }
 
        List labels = entity.getLabels();
        if (labels != null) {
            stmt.bindString(7, labelsConverter.convertToDatabaseValue(labels));
        }
 
        List photoUrl = entity.getPhotoUrl();
        if (photoUrl != null) {
            stmt.bindString(8, photoUrlConverter.convertToDatabaseValue(photoUrl));
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, BossSimpleModel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String head = entity.getHead();
        if (head != null) {
            stmt.bindString(3, head);
        }
 
        String role = entity.getRole();
        if (role != null) {
            stmt.bindString(4, role);
        }
        stmt.bindLong(5, entity.getTop() ? 1L: 0L);
 
        Long updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(6, updateTime);
        }
 
        List labels = entity.getLabels();
        if (labels != null) {
            stmt.bindString(7, labelsConverter.convertToDatabaseValue(labels));
        }
 
        List photoUrl = entity.getPhotoUrl();
        if (photoUrl != null) {
            stmt.bindString(8, photoUrlConverter.convertToDatabaseValue(photoUrl));
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public BossSimpleModel readEntity(Cursor cursor, int offset) {
        BossSimpleModel entity = new BossSimpleModel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // head
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // role
            cursor.getShort(offset + 4) != 0, // top
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // updateTime
            cursor.isNull(offset + 6) ? null : labelsConverter.convertToEntityProperty(cursor.getString(offset + 6)), // labels
            cursor.isNull(offset + 7) ? null : photoUrlConverter.convertToEntityProperty(cursor.getString(offset + 7)) // photoUrl
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, BossSimpleModel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setHead(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRole(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTop(cursor.getShort(offset + 4) != 0);
        entity.setUpdateTime(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setLabels(cursor.isNull(offset + 6) ? null : labelsConverter.convertToEntityProperty(cursor.getString(offset + 6)));
        entity.setPhotoUrl(cursor.isNull(offset + 7) ? null : photoUrlConverter.convertToEntityProperty(cursor.getString(offset + 7)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(BossSimpleModel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(BossSimpleModel entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(BossSimpleModel entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}