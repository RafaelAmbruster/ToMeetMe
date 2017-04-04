package com.app.tomeetme.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.helper.manager.ConfigurationManager;
import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.BusinessCategory;
import com.app.tomeetme.model.BusinessPictures;
import com.app.tomeetme.model.User;
import com.app.tomeetme.model.BusinessReview;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class AppDatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME = "tomeetme.db3";
    private static final int DATABASE_VERSION = 1;

    public AppDatabaseHelper(Context context) {
        super(context,  ConfigurationManager.getInstance().getPath(3) + DATABASE_NAME, null,
                DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {

            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, BusinessCategory.class);
            TableUtils.createTable(connectionSource, Business.class);
            TableUtils.createTable(connectionSource, BusinessPictures.class);
            TableUtils.createTable(connectionSource, BusinessReview.class);

        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {

            //Removing
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, BusinessCategory.class, true);
            TableUtils.dropTable(connectionSource, Business.class, true);
            TableUtils.dropTable(connectionSource, BusinessPictures.class, true);
            TableUtils.dropTable(connectionSource, BusinessReview.class, true);

            //Creating
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, BusinessCategory.class);
            TableUtils.createTable(connectionSource, Business.class);
            TableUtils.createTable(connectionSource, BusinessPictures.class);
            TableUtils.createTable(connectionSource, BusinessReview.class);
        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
    }

    @Override
    public void close() {
        super.close();
    }
}
