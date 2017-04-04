package com.app.tomeetme.helper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.app.tomeetme.ToMeetMeApplication;
import com.app.tomeetme.R;
import com.google.android.gms.maps.GoogleMap;

public class Preferences {
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public Preferences(Context context) {
        if (context == null) context = ToMeetMeApplication.getContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
    }

    public void clearPreferences() {
        Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public int getMapType() {
        String key = mContext.getString(R.string.prefs_key_map_type);
        int value = mSharedPreferences.getInt(key, GoogleMap.MAP_TYPE_NORMAL);
        return value;
    }

    public void setMapType(int mapType) {
        String key = mContext.getString(R.string.prefs_key_map_type);
        Editor editor = mSharedPreferences.edit();
        editor.putInt(key, mapType);
        editor.commit();
    }
}
