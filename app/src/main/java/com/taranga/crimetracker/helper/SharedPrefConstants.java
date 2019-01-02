package com.taranga.crimetracker.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.taranga.crimetracker.model.CrimeDetailsModel;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefConstants {

    private static final String TAG = SharedPrefConstants.class.getSimpleName();

    public static String PREFS_NAME = "Crime Reporter";

    public static String MAP_ZOOM_LEVEL = "Map Zoom Level";

    public static String SEARCH_DISTANCE = "Search Distance";

    public static String LOGIN_STATUS = "Login Status";

    public static String USER_NAME = "User Name";

    /**
     * KEY String to save and access CURRENT_LATITUDE from shared preferences
     */
    public static final String CURRENT_LATITUDE = "CURRENT_LATITUDE";

    /**
     * KEY String to save and access CURRENT_LONGITUDE from shared preferences
     */
    public static final String CURRENT_LONGITUDE = "CURRENT_LONGITUDE";

    /**
     * KEY String to save and access NEW_CRIME_LOCATION from shared preferences
     */
    public static final String NEW_CRIME_LOCATION = "NEW_CRIME_LOCATION";

    /**
     * KEY String to save and access CURRENT_CRIME_DETAILS from shared preferences
     */
    public static final String CURRENT_CRIME_DETAILS = "CURRENT_CRIME_DETAILS";

    /**
     * KEY String to save and access CRIME_ALERT_FRAGMENT from shared preferences
     */
    public static final String CRIME_ALERT_FRAGMENT = "CRIME_ALERT_FRAGMENT";

    /**
     * KEY String to save and access IS_FIRST_LOGIN from shared preferences
     */
    public static final String IS_FIRST_LOGIN = "IS_FIRST_LOGIN";


    // Getter and Setter method for CURRENT_CRIME_DETAILS

    public static void saveCurrentCrimeDetails(Context context, CrimeDetailsModel crimeDetailsModel) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        sharedPreferences.edit().putString(CURRENT_CRIME_DETAILS, gson.toJson(crimeDetailsModel, CrimeDetailsModel.class)).apply();
    }

    public static CrimeDetailsModel getCurrentCrimeDetails(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(CURRENT_CRIME_DETAILS, "");
        Gson gson = new Gson();
        CrimeDetailsModel crimeDetailsModel = new CrimeDetailsModel();
        if (!TextUtils.isEmpty(json)) {
            crimeDetailsModel = gson.fromJson(json, CrimeDetailsModel.class);
        }
        return crimeDetailsModel;
    }

    // Getter and Setter method for CURRENT_LATITUDE and CURRENT_LONGITUDE
    public static void saveCurrentLatitudeAndLongitude(Context context, Double latitude, Double longitude) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(CURRENT_LATITUDE, String.valueOf(latitude)).apply();
        sharedPreferences.edit().putString(CURRENT_LONGITUDE, String.valueOf(longitude)).apply();
    }

    public static LatLng getCurrentLatitudeAndLongitude(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);

        String latitude = sharedPreferences.getString(CURRENT_LATITUDE, "");
        String longitude = sharedPreferences.getString(CURRENT_LONGITUDE, "");

        if (latitude.isEmpty() && longitude.isEmpty()) {
            return new LatLng(0.0, 0.0);
        } else {
            return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        }
    }

    // Getter and Setter method for SEARCH_DISTANCE Value
    public static void saveMaxDistanceForCrimeLocationSearching(Context context, String distance) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(SEARCH_DISTANCE, distance).apply();
    }

    public static String getDistanceForSearching(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SEARCH_DISTANCE, "");
    }

    public static void saveCrimeAlertFragmentStatus(Context context, boolean isActive) {
        SharedPreferences prefs = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(CRIME_ALERT_FRAGMENT, isActive).apply();
    }

    public static boolean getCrimeAlertFragmentStatus(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(CRIME_ALERT_FRAGMENT, false);
    }

    // Getter and Setter for saving IS_FIRST_LOGIN value
    public static void saveFirstTimeLogin(Context context, boolean isFirst) {
        SharedPreferences prefs = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(IS_FIRST_LOGIN, true).apply();
    }

    public static boolean getFirstTimeLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(IS_FIRST_LOGIN, false);
    }

    // Getter and Setter method for SEARCH_DISTANCE Value
    public static void saveMapZoomLevel(Context context, int mapZoomLevel) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(MAP_ZOOM_LEVEL, mapZoomLevel).apply();
    }

    public static int getMapZoomLevel(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(MAP_ZOOM_LEVEL, 0);
    }

    // Getter and Setter method for SEARCH_DISTANCE Value
    public static void saveLoginStatus(Context context, boolean isLogined) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(LOGIN_STATUS, isLogined).apply();
    }

    public static boolean getLoginStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(LOGIN_STATUS, false);
    }

    public static void removeLoginStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(LOGIN_STATUS).apply();
    }


    // Getter and Setter method for USER_NAME Value
    public static void saveUserName(Context context, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(USER_NAME, userName).apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_NAME, "");
    }

    public static void removeUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(USER_NAME).apply();
    }
}
