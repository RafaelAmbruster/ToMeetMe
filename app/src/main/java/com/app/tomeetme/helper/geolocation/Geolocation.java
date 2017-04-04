package com.app.tomeetme.helper.geolocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.app.tomeetme.ToMeetMeApplication;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;


public class Geolocation implements LocationListener {
    private static final int LOCATION_AGE = 60000 * 5;
    private static final int LOCATION_TIMEOUT = 30000;

    private WeakReference<GeolocationListener> mListener;
    private LocationManager mLocationManager;
    private Location mCurrentLocation;
    private Timer mTimer;

    public Geolocation(LocationManager locationManager, GeolocationListener listener) {
        mLocationManager = locationManager;
        mListener = new WeakReference<>(listener);
        mTimer = new Timer();
        init();
    }

    @Override
    public void onLocationChanged(Location location) {

        long timeDelta = System.currentTimeMillis() - location.getTime();
        if (timeDelta > LOCATION_AGE) {
            return;
        }

        mCurrentLocation = new Location(location);
        stop();
        GeolocationListener listener = mListener.get();
        if (listener != null && location != null)
            listener.onGeolocationRespond(Geolocation.this, mCurrentLocation);
    }


    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
            case LocationProvider.AVAILABLE:
                break;
        }
    }

    public void stop() {
        if (mTimer != null) mTimer.cancel();
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(ToMeetMeApplication.getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(ToMeetMeApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocationManager.removeUpdates(this);
            mLocationManager = null;
        }
    }

    private void init() {
        Location lastKnownLocation = getLastKnownLocation(mLocationManager);
        if (lastKnownLocation != null) {
            onLocationChanged(lastKnownLocation);
        }
        if (mCurrentLocation == null) {
            TimerTask task = new TimerTask() {
                public void run() {
                    if (mCurrentLocation == null) {
                        stop();
                        GeolocationListener listener = mListener.get();
                        if (listener != null) listener.onGeolocationFail(Geolocation.this);
                    }
                }
            };
            mTimer.schedule(task, LOCATION_TIMEOUT);

            if (ActivityCompat.checkSelfPermission(ToMeetMeApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(ToMeetMeApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0.0f, this);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            try {

                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0l, 0.0f, this);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private Location getLastKnownLocation(LocationManager locationManager) {

        if (ActivityCompat.checkSelfPermission(ToMeetMeApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ToMeetMeApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        long timeNet = 0l;
        long timeGps = 0l;

        if (locationNet != null) {
            timeNet = locationNet.getTime();
        }

        if (locationGps != null) {
            timeGps = locationGps.getTime();
        }

        if (timeNet > timeGps) return locationNet;
        else return locationGps;
    }
}
