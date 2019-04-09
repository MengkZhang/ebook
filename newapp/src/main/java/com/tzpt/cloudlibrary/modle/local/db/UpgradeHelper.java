package com.tzpt.cloudlibrary.modle.local.db;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Administrator on 2018/8/17.
 */

public class UpgradeHelper extends DaoMaster.OpenHelper {

    UpgradeHelper(Context context, String name) {
        super(context, name, null);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        if (oldVersion > 3) {
            if (newVersion == 5) {
                MigrationHelper.getInstance().migrate(db,
                        DownInfoColumnsDao.class,
                        VideoColumnsDao.class,
                        VideoPlayColumnsDao.class,
                        VideoSetColumnsDao.class);
            } else {
                MigrationHelper.getInstance().migrate(db,
                        BookColumnsDao.class,
                        BookMarkColumnsDao.class,
                        DownInfoColumnsDao.class,
                        VideoColumnsDao.class,
                        VideoPlayColumnsDao.class,
                        VideoSetColumnsDao.class);
            }

        } else {
            DaoMaster.dropAllTables(db, true);
            DaoMaster.createAllTables(db, false);
        }
    }
}
