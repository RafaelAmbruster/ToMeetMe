package com.app.tomeetme.helper.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

public class CustomLocationApp implements LocationListener {

    public static String lastLat = null;
    public static String lastLng = null;
    public LocationManager locationManager = null;
    public Context context;
    public LocationUtilsCallBack callBack;

    public CustomLocationApp(Context context, LocationUtilsCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    public void LoadLocation() {

        try {
            if (lastLat != null && lastLng != null) {
                callBack.onLocationFound(lastLat, lastLng);
            } else {
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                }else
                    callBack.onGPSNotFound();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        lastLat = "" + location.getLatitude();
        lastLng = "" + location.getLongitude();

        try {
            if (callBack != null)
                callBack.onLocationFound(lastLat, lastLng);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public interface LocationUtilsCallBack {
         void onLocationFound(String lng, String lat);

         void onGPSNotFound();
    }
}
