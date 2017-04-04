package com.app.tomeetme.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.helper.manager.ConfigurationManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class AppDatabaseManager {

    private static AppDatabaseManager instance;

    private AppDatabaseHelper helper;

    private AppDatabaseManager(Context ctx) {
        helper = new AppDatabaseHelper(ctx);
    }

    public static void init(Context ctx) {
        if (null == instance) {
            instance = new AppDatabaseManager(ctx);
        }
    }

    public static AppDatabaseManager getInstance() {
        return instance;
    }

    public AppDatabaseHelper getHelper() {
        return helper;
    }

    public void Export(String path) {

        try {
            File sd = new File(path);
            if (sd.canWrite()) {

                String currentDBPath;
                currentDBPath = ConfigurationManager.getInstance().getPath(3) + AppDatabaseHelper.DATABASE_NAME;
                String backupDBPath = AppDatabaseHelper.DATABASE_NAME;
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();

                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (FileNotFoundException e) {
            LogManager.getInstance().error(getClass().getCanonicalName() + " Exporting Database!", e.getMessage());
        } catch (IOException e) {
            LogManager.getInstance().error(getClass().getCanonicalName() + " Exporting Database!", e.getMessage());
        }
    }

    public boolean Import(Context c) {

        boolean flag = Create();
        boolean ready = true;

        if (!flag) {
            try {

                AssetManager assetManager = c.getAssets();
                String assets[];
                assets = assetManager.list("data");
                OutputStream databaseOutputStream;
                databaseOutputStream = new FileOutputStream(ConfigurationManager.getInstance().getPath(3) + AppDatabaseHelper.DATABASE_NAME);
                InputStream databaseInputStream;
                byte[] buffer = new byte[1024];

                for (String str : assets) {
                    databaseInputStream = c.getAssets().open("data/" +
                            str);
                    while ((databaseInputStream.read(buffer)) > 0) {
                        databaseOutputStream.write(buffer);
                    }
                    databaseInputStream.close();
                }

                databaseOutputStream.flush();
                databaseOutputStream.close();

            } catch (IOException e) {
                ready = false;
                e.printStackTrace();
                LogManager.getInstance().error(getClass().getCanonicalName() + " Importing Database!", e.getMessage());
            }
        }
        return ready;
    }

    private boolean Create() {
        boolean flag;

        File db;
        db = new File(ConfigurationManager.getInstance().getPath(3) + AppDatabaseHelper.DATABASE_NAME);
        flag = db.exists();

        try {
            if (!flag) {
                File parent = db.getParentFile();
                if (parent != null)
                    parent.mkdirs();
            }
        } catch (Exception ex) {
            LogManager.getInstance().error(getClass().getCanonicalName() + " Creating File!!", ex.getMessage());
        }

        return flag;
    }
}