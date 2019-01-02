package com.taranga.crimetracker.services;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.taranga.crimetracker.helper.AppConstants;
import com.taranga.crimetracker.helper.SharedPrefConstants;
import com.taranga.crimetracker.local_databse.LocalDatabaseManager;
import com.taranga.crimetracker.model.CrimeDetailsModel;
import com.taranga.crimetracker.model.event_bus.DataEvent;
import com.taranga.crimetracker.view.main.CrimeReporterApplication;
import com.taranga.crimetracker.view.main.MainActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class TrackerService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GpsStatus.Listener,
        LocationListener {

    private static final String TAG = "TrackerService";
    private static boolean isServiceRunning;
    private GoogleApiClient googleApiClient;
    private Location locationPending;
    private CrimeReporterApplication app;

    private String tempPreviousTimeStamp;
    private double tempPreviousLatitude = 0.1;
    private double tempPreviousLongitude = 0.1;

    private String uniqueBatchId;

    private String startTime, userId;
    private Context context;

    private ArrayList<LatLng> latLngList = new ArrayList<>();

    public static boolean isServiceRunning() {
        return isServiceRunning;
    }

    private static void setIsServiceRunning(boolean isServiceRunning) {
        TrackerService.isServiceRunning = isServiceRunning;
    }

    public TrackerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stopForeground(true);
        this.context = this;

        app = (CrimeReporterApplication) getApplication();

        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(12345);


        Intent notificationIntent = new Intent(this, MainActivity.class);

      /*  PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(context)
                .setContentTitle("ನಿಮ್ಮ ಪ್ರಯಾಣ ಆರಂಭವಾಗಿದೆ..")
                .setContentText(" ನಿಮ್ಮನ್ನು ಟ್ರ್ಯಾಕ್ ಮಾಡುತ್ತಿದ್ದೇವೆ.")
                .setSmallIcon(R.drawable.ic_crime)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setTicker("ಟ್ರಾಕಿಂಗ್ ಸ್ಟಾರ್ಟ್ ಅಲರ್ಟ್..")
                .setPriority(Notification.PRIORITY_MAX)
                .build();

        startForeground(1337, notification);*/

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createGoogleApiClient();
        TrackerService.setIsServiceRunning(true);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: trackerService");
        stopLocationUpdates();
        TrackerService.setIsServiceRunning(false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        locationPending = location;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        if (!(latitude == 0.0) && !(longitude == 0.0)) {
            LatLng coordinate = new LatLng(latitude, longitude);
            SharedPrefConstants.saveCurrentLatitudeAndLongitude(getApplicationContext(), latitude, longitude);
            CrimeDetailsModel nearestModel = LocalDatabaseManager.getNearestCrimeSpotLocation(getApplicationContext(), coordinate);
            if (nearestModel != null) {

                Log.d(TAG, "onLocationChanged: nearmodel: " + nearestModel.toString());
                CrimeDetailsModel oldModel = SharedPrefConstants.getCurrentCrimeDetails(getApplicationContext());

                Log.d(TAG, "onLocationChanged: 1");
                if (oldModel.getId() != null) {
                    Log.d(TAG, "onLocationChanged: 3");
                    Log.d(TAG, "onLocationChanged: oldModel:" + oldModel.toString());
                    boolean isSameData = nearestModel.equals(oldModel);
                 /*   if (!isSameData) {
                        Log.d(TAG, "onLocationChanged: 4");
                        if (!SharedPrefConstants.getCrimeAlertFragmentStatus(TrackerService.this)) {
                            Log.d(TAG, "onLocationChanged: 5");
                            EventBus.getDefault().post(new DataEvent(nearestModel));

                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra(AppConstants.NEW_LOCATION_DETAILS, true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }*/
                } else {
                    Log.d(TAG, "onLocationChanged: 2");
                    SharedPrefConstants.saveCurrentCrimeDetails(getApplicationContext(), nearestModel);
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    private void createGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        connectGoogleApiClient();
    }

    private void connectGoogleApiClient() {
        if (googleApiClient != null) {
            if (!(googleApiClient.isConnected() || googleApiClient.isConnecting())) {
                googleApiClient.connect();
            } else {
                Log.d(TAG, "Client is connected");
                startLocationUpdates();
            }
        } else {
            Log.d(TAG, "Client is null");
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = app.createLocationRequest();
        registerLocationUpdates(locationRequest);
    }

    private void stopLocationUpdates() {
        removeLocationUpdates();
    }

    private void registerLocationUpdates(LocationRequest locationRequest) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        } catch (Exception e) {
            e.printStackTrace();
            connectGoogleApiClient();
        }
    }

    private void removeLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                Log.d(TAG, "onGpsStatusChanged started");
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                Log.d(TAG, "onGpsStatusChanged stopped");
                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                Log.d(TAG, "onGpsStatusChanged first fix");
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                break;
        }
    }

    private boolean calculateAverageDistance(long timeDifferenceInSecond, double distanceInKilometers) {

        double totalDistance = ((timeDifferenceInSecond * AppConstants.AVERAGE_DISTANCE) / AppConstants.AVERAGE_SECONDS);

        Log.d(TAG, "calculateAverageDistance: average Distance: " + totalDistance);
        Log.d(TAG, "calculateAverageDistance: getting distance: " + distanceInKilometers);
        Log.d(TAG, "calculateAverageDistance: time taken: " + timeDifferenceInSecond);

        if (totalDistance >= distanceInKilometers) {
            return true;
        }
        return false;
    }
}