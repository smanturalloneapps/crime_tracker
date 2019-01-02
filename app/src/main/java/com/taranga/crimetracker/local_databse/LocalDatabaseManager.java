package com.taranga.crimetracker.local_databse;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.taranga.crimetracker.helper.SharedPrefConstants;
import com.taranga.crimetracker.helper.Utils;
import com.taranga.crimetracker.model.CrimeDetailsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class LocalDatabaseManager {

    private static HashMap<LatLng, CrimeDetailsModel> getCrimeSpotLocations() {

        List<CrimeDetailsModel> crimeDetailsModelList = SQLite.select().from(CrimeDetailsModel.class).queryList();

        HashMap<LatLng, CrimeDetailsModel> crimeSpotLocations = new HashMap<>();

        if (crimeDetailsModelList.size() > 0) {
            for (CrimeDetailsModel crimeDetailsModel : crimeDetailsModelList) {
                crimeSpotLocations.put(new LatLng(crimeDetailsModel.getLatitude(), crimeDetailsModel.getLongitude()), crimeDetailsModel);
            }
        }
        return crimeSpotLocations;
    }

    public static HashMap<LatLng, CrimeDetailsModel> searchNearestCrimeSpotLocations(Context context, LatLng currentLatLng) {

        HashMap<LatLng, CrimeDetailsModel> crimeDetailsNearestHashMap = null;
        try {
            crimeDetailsNearestHashMap = new SearchNearestCrimeSpotLocationsBackgroundAsync(context).execute(currentLatLng).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return crimeDetailsNearestHashMap;
    }

    public static CrimeDetailsModel getNearestCrimeSpotLocation(Context context, LatLng currentLatLng) {

        CrimeDetailsModel crimeDetailsModel = null;
        try {
            crimeDetailsModel = new getNearestCrimeSpotLocationBackgroundAsync(context).execute(currentLatLng).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return crimeDetailsModel;
    }

    // SearchNearestCrimeSpotLocationsBackgroundAsync in background using AsyncTask
    private static class SearchNearestCrimeSpotLocationsBackgroundAsync extends AsyncTask<LatLng, HashMap<LatLng, CrimeDetailsModel>, HashMap<LatLng, CrimeDetailsModel>> {
        private Context context;

        public SearchNearestCrimeSpotLocationsBackgroundAsync(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: preexe 1 ");
        }

        // doInBackground() method executed after onPreExecute() method and before onPostExecute() method
        // here we can doing image conversion into base64 and set it into respected modules with other data variables
        @SafeVarargs
        @Override
        protected final HashMap<LatLng, CrimeDetailsModel> doInBackground(LatLng... currentLatLng) {
            Log.d(TAG, "doInBackground: 1");
            HashMap<LatLng, CrimeDetailsModel> crimeDetailsMainHashMap = getCrimeSpotLocations();
            HashMap<LatLng, CrimeDetailsModel> crimeDetailsNearestHashMap = new HashMap<>();
            List<LatLng> latLongs = new ArrayList<>(getCrimeSpotLocations().keySet());
            Log.d(TAG, "searchNearestLocation: latlngs: " + latLongs);

            for (LatLng latLng : latLongs) {
                double distance = Utils.distanceCal(currentLatLng[0].latitude, currentLatLng[0].longitude, latLng.latitude, latLng.longitude);
                if (distance <= Double.parseDouble(SharedPrefConstants.getDistanceForSearching(context))) {
                    crimeDetailsNearestHashMap.put(latLng, crimeDetailsMainHashMap.get(latLng));
                }
            }
            return crimeDetailsNearestHashMap;
        }

        @Override
        protected void onPostExecute(HashMap<LatLng, CrimeDetailsModel> crimeDetailsNearestHashMap) {
            super.onPostExecute(crimeDetailsNearestHashMap);
            Log.d(TAG, "onPostExecute: post execute 1");
            getValue(crimeDetailsNearestHashMap);
        }

        public static HashMap<LatLng, CrimeDetailsModel> getValue(HashMap<LatLng, CrimeDetailsModel> crimeDetailsNearestHashMap) {
            Log.d(TAG, "getValue: getvlue method 1");
            return crimeDetailsNearestHashMap;
        }

    }


    // getNearestCrimeSpotLocationBackgroundAsync in background using AsyncTask
    private static class getNearestCrimeSpotLocationBackgroundAsync extends AsyncTask<LatLng, CrimeDetailsModel, CrimeDetailsModel> {
        private Context context;

        public getNearestCrimeSpotLocationBackgroundAsync(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: preexe 2 ");
        }

        @SafeVarargs
        @Override
        protected final CrimeDetailsModel doInBackground(LatLng... currentLatLng) {
            Log.d(TAG, "doInBackground: 2");
            HashMap<LatLng, CrimeDetailsModel> crimeDetailsMainHashMap = getCrimeSpotLocations();
            CrimeDetailsModel crimeDetailsModel = new CrimeDetailsModel();
            List<LatLng> latLongs = new ArrayList<>(getCrimeSpotLocations().keySet());

            double smallestDistance = -1;
            LatLng closestLocation = new LatLng(0.0, 0.0);

            for (LatLng latLng : latLongs) {
                double distance = Utils.distanceCal(currentLatLng[0].latitude, currentLatLng[0].longitude, latLng.latitude, latLng.longitude);
                if (distance <= Double.parseDouble(SharedPrefConstants.getDistanceForSearching(context))) {
                    if (smallestDistance == -1 || distance < smallestDistance) {
                        closestLocation = latLng;
                        smallestDistance = distance;
                    }
                }
            }

            if (!(closestLocation.latitude == 0.0) && !(closestLocation.longitude == 0.0) && !(smallestDistance == -1)) {
                crimeDetailsModel = crimeDetailsMainHashMap.get(closestLocation);
            }
            return crimeDetailsModel;
        }

        @Override
        protected void onPostExecute(CrimeDetailsModel crimeDetailsModel) {
            super.onPostExecute(crimeDetailsModel);
            Log.d(TAG, "onPostExecute: post execute 2");
            getValue(crimeDetailsModel);
        }

        public static CrimeDetailsModel getValue(CrimeDetailsModel crimeDetailsModel) {
            Log.d(TAG, "getValue: getvlue method 2");
            return crimeDetailsModel;
        }

    }

    public static boolean checkLoginValidation(String userName, String password) {
        return userName != null && password != null && (userName.equals("Varsha") && password.equals("Varsha123") || userName.equals("Anam") && password.equals("Anam246") || userName.equals("Admin") && password.equals("Admin@123") || userName.equals("Vishal") && password.equals("Vishal345"));
    }
}
