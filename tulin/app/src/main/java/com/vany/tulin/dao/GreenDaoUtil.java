package com.vany.tulin.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.vany.tulin.dto.DaoMaster;
import com.vany.tulin.dto.DaoSession;


/**
 * Created by van元 on 2017/2/17.
 */

public class GreenDaoUtil {
    private static GreenDaoUtil greenDaoUtil;
    private static SQLiteDatabase db;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;

    /**
     * 获取DaoSession
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {
        if (mDaoMaster == null) {
            initGreenDao(context);
        }
        return mDaoSession;
    }

    /**
     * 获取DaoMaster
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (mDaoMaster == null) {
            initGreenDao(context);
        }
        return mDaoMaster;
    }

    /**
     * 获取SQLiteDatabase
     * @param context
     * @return
     */
    public static SQLiteDatabase getDb(Context context) {
        if (db == null) {
            initGreenDao(context);
        }
        return db;
    }
    private static void initGreenDao(Context context) {
            db = new DaoMaster.DevOpenHelper(context, "word-db", null).getWritableDatabase();
            mDaoMaster = new DaoMaster(db);
            mDaoSession = mDaoMaster.newSession();
    }
}
