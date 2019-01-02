package com.taranga.crimetracker.view.main;

import android.app.Application;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.google.android.gms.location.LocationRequest;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by SHIVU on 2/18/2018.
 */

public class CrimeReporterApplication extends MultiDexApplication {
    private Location startLocation;
    private static final String KEY_REQUEST_DATA_NAME = "KEY_REQUEST_DATA_NAME";

    @Override
    public void onCreate() {
        super.onCreate();

        FlowManager.init(new FlowConfig.Builder(this).build());
    }

    private boolean retrieveLocationRequestData() {

        String name = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_REQUEST_DATA_NAME, null);
        if (!TextUtils.isEmpty(name)) {
//            locationRequestData = LocationRequestData.valueOf(name);
            return true;
        }
        return false;
    }

    public Location getStartLocation()
    {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public LocationRequest createLocationRequest() {
        final int SMALLEST_INTERVAL = 1000 * 30; // 30 Sec
        final int FASTEST_INTERVAL = 1000 * 60; // 60Sec
        final float SMALLEST_DISPLACEMENT = 10f; // 10 meters

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(SMALLEST_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        return locationRequest;
    }
}
