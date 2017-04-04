package com.app.tomeetme;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import com.app.tomeetme.data.AppDatabaseManager;
import com.app.tomeetme.helper.log.LogManager;
import com.orhanobut.logger.Logger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ToMeetMeApplication extends MultiDexApplication {

    private static ToMeetMeApplication instance;
    public static String TAG = "2MeetMeApp";

    public ToMeetMeApplication() {
        instance = this;
    }

    public static synchronized ToMeetMeApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Logger.init("2MeetMe").hideThreadInfo().setMethodCount(3).setMethodOffset(2);
        AppDatabaseManager.init(getApplicationContext());
        LogManager.init();
        printKeyHash();
    }

    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.app.tomeetme", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                LogManager.getInstance().info("SHA: ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static Context getContext() {
        return instance;
    }
}
