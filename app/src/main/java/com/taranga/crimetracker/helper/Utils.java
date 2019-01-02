package com.taranga.crimetracker.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by SHIVU on 2/18/2018.
 */

public class Utils {

    private static final int PERMISSION_REQUEST_LOCATION = 111;
    private static final String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final String TAG = Utils.class.getSimpleName();

    public static String[] getPermissionsLocation() {
        return PERMISSIONS_LOCATION;
    }

    public static int getPermissionRequestLocation() {
        return PERMISSION_REQUEST_LOCATION;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getCurrentDateStamp() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(c.getTime());
    }

    public static String getCurrentTimeStamp() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a", Locale.getDefault());
        return sdf.format(c.getTime());
    }

    public static boolean checkPermission(Context context) {
        int r1 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int r2 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        return r1 == PackageManager.PERMISSION_GRANTED && r2 == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkLocationPermission(Context context) {
        if (android.support.v4.BuildConfig.DEBUG)
            Log.d(TAG, "Checking location permission");
        int r1 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int r2 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (android.support.v4.BuildConfig.DEBUG)
            Log.d(TAG, ": " + (r1 == PackageManager.PERMISSION_GRANTED && r2 == PackageManager.PERMISSION_GRANTED));
        return r1 == PackageManager.PERMISSION_GRANTED && r2 == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isGooglePlayServicesAvailable(Context context, Activity activity) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int status = googleAPI.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            googleAPI.getErrorDialog(activity, status, 0).show();
            return false;
        }
    }

    // hide softkeyboard
    public static void hideSoftKeyboard(@Nullable Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Calculates the distance between two location points using the ‘Haversine’ formula.
     * Formulas are for calculations on the basis of a spherical earth
     * (ignoring ellipsoidal effects) – which is accurate enough for most purposes
     *
     * @param lat1 Previous location latitude
     * @param lng1 Previous location longitude
     * @param lat2 New location latitude
     * @param lng2 New location longitude
     * @return distance between two locations
     */
    public static float distFromCoordinates(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (float) (earthRadius * c);
    }

    public static double distanceCal(Double latitude, Double longitude, double e, double f) {
        double d2r = Math.PI / 180;
        double dlong = (longitude - f) * d2r;
        double dlat = (latitude - e) * d2r;
        double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(e * d2r)
                * Math.cos(latitude * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367 * c;
        return d;

    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static ArrayList<String> getCrimeTitles() {

        ArrayList<String> crimeList = new ArrayList<>();
        crimeList.add("Select Crime");
        crimeList.add("ATTEMPT TO MURDER");
        crimeList.add("BURGLARY");
        crimeList.add("CHAIN SNATCHING");
        crimeList.add("DACOITY");
        crimeList.add("KIDNAPPING");
        crimeList.add("MURDER");
        crimeList.add("RAPE");
        crimeList.add("ROBBERY");
        crimeList.add("HIT AND RUN");
        crimeList.add("Other");

        return crimeList;
    }
}
