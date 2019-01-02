package com.taranga.crimetracker.view.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taranga.crimetracker.R;
import com.taranga.crimetracker.helper.AppConstants;
import com.taranga.crimetracker.helper.SharedPrefConstants;
import com.taranga.crimetracker.helper.Utils;
import com.taranga.crimetracker.local_databse.LocalDatabaseManager;
import com.taranga.crimetracker.model.CrimeDetailsModel;
import com.taranga.crimetracker.model.event_bus.DataEvent;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        OnMapReadyCallback {

    // region Variable declarations

    private static String TAG = MainFragment.class.getSimpleName();
    private View rootView;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 999;
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    private final int LOCATION_SETTINGS_CODE = 111;
    private CoordinatorLayout mainFragmentCoordinatorLayout;
    private GoogleMap googleMap;
    private LatLng centerLatLong;
    private SupportMapFragment mapFragment;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Marker marker;
    private Location mlocation;
    private float bearingvalue = 0;

    private OnFragmentInteractionListener listener;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildGoogleApiClient();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(8000);
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_track);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map_track, mapFragment).commit();
        }

        MapsInitializer.initialize(getContext());

        checkPlayServices();

        if (!Utils.isLocationEnabled(getContext())) {
            showGPSDisabledAlertToUser();
        }

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            RxPermissions.getInstance(getActivity())
                    .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(Boolean granted) {
                            if (granted) { // Always true for pre-M
                                Log.d(TAG, "onCreate: Permission granted");
                            } else {
                                Log.d(TAG, "setUpViews: Permission denied");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

            if (Utils.isLocationEnabled(getContext())) {
                initializeMap();
            } else {
                // Notify user to enable GPS
                showGPSDisabledAlertToUser();
            }
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.location_support), Toast.LENGTH_SHORT).show();
        }
        setUpViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();

       /* if (googleMap != null) {
            googleMap.clear();

            // add markers from database to the map
        }*/
        try {
            googleApiClient.connect();
//            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);
            locationRequest = null;
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setUpViews() {
        mainFragmentCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.map_coordinator_layout);
    }

    private void initializeMap() {
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(false);

        /*
        * Updates the bounds being used by the auto complete adapter based on the position of the
        * mMap.
        * */
        //noinspection deprecation
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                {
                    centerLatLong = cameraPosition.target;

                    try {
                        Location mLocation = new Location("Center");
                        mLocation.setLatitude(centerLatLong.latitude);
                        mLocation.setLongitude(centerLatLong.longitude);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                LatLng myLat = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLat));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null) {
                // check if googleMap is created successfully or not
                if (googleMap != null) {
                    LatLng latLng;

                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    int zoomLevel = SharedPrefConstants.getMapZoomLevel(getContext());

                    if (zoomLevel == 0) {
                        zoomLevel = 16;
                        SharedPrefConstants.saveMapZoomLevel(getContext(), 16);
                    }

                    googleMap.setMyLocationEnabled(true);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)
                            .zoom(zoomLevel)
                            .tilt(45)
                            .build();

                    this.mlocation = location;
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    try {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        if (!(latitude == 0.0) && !(longitude == 0.0)) {
                            LatLng coordinate = new LatLng(latitude, longitude);
                            SharedPrefConstants.saveCurrentLatitudeAndLongitude(getContext(), coordinate.latitude, coordinate.longitude);
                            HashMap<LatLng, CrimeDetailsModel> nearestLocationsMap = LocalDatabaseManager.searchNearestCrimeSpotLocations(getContext(), coordinate);

                            if (nearestLocationsMap.size() > 0) {
                                ArrayList<LatLng> nearestLatLngs = new ArrayList<>(nearestLocationsMap.keySet());
                                List<CrimeDetailsModel> nearestModelList = new ArrayList<>(nearestLocationsMap.values());
                                if (nearestModelList.size() > 0) {
                                    addMarkToMapWithPlaces(nearestModelList);
//                                    addMarkToMap(nearestLatLngs);
                                    Toast.makeText(getContext(), "Nearest Crime Locations: " + nearestLocationsMap.size(), Toast.LENGTH_SHORT).show();
                                    nearestLocationsMap.clear();
                                    nearestLatLngs.clear();
                                }
                            } else {
                                Toast.makeText(getContext(), "Nearest Crime Locations: " + nearestLocationsMap.size(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Unable to create map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // region Permission requests

    /**
     * Checks the device if Google play services are installed or not.
     * Google play services are required to display google maps.
     *
     * @return true if play services installed and false if not.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(getActivity(), resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Displays an alert dialog and requests the user to enable the GPS service.
     * GPS is required to fetch the accurate current location of the user.
     */
    private void showGPSDisabledAlertToUser() {
        Log.d(TAG, "showGPSDisabledAlertToUser: GPS request");
        LocationSettingsRequest locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)
                .build();

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequestBuilder);

        result.setResultCallback(new ResolvingResultCallbacks<LocationSettingsResult>(getActivity(), 0) {
            @Override
            public void onSuccess(@NonNull LocationSettingsResult locationSettingsResult) {
                {
                    final Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            Log.d(TAG, "GPS enabled after request");
                            initializeMap();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(getActivity(), LOCATION_SETTINGS_CODE);
                            } catch (IntentSender.SendIntentException ignored) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.e(TAG, "Settings change unavailable. We have no way to fix the settings so we won't show the dialog.");
                            break;
                    }
                }
            }

            @Override
            public void onUnresolvableFailure(@NonNull Status status) {

            }
        });
    }

    /**
     * Used to check the result of the check of the user location settings
     *
     * @param requestCode code of the request made
     * @param resultCode  code of the result of that request
     * @param intent      intent with further information
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case LOCATION_SETTINGS_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        initializeMap();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Snackbar.make(mainFragmentCoordinatorLayout, getResources().getString(R.string.enable_gps), Snackbar.LENGTH_LONG)
                                .setAction(getResources().getString(R.string.enable), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showGPSDisabledAlertToUser();
                                    }
                                }).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void addMarkToMapWithPlaces(List<CrimeDetailsModel> modelList) {
        if (modelList.size() > 0) {
            googleMap.clear();
            for (CrimeDetailsModel model : modelList) {
                BitmapDescriptor bitmapMarker;
                bitmapMarker = BitmapDescriptorFactory.fromResource(R.drawable.crime_location);
                googleMap.addMarker(new MarkerOptions().position(new LatLng(model.getLatitude(), model.getLongitude())).title(model.getCrime()).snippet(model.getAddress()).icon(bitmapMarker).flat(false).anchor(0.5f, 0.5f));
            }
        }
    }


    // This method will be called when a ResultEvent is posted.
    @SuppressWarnings(AppConstants.UNUSED)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(DataEvent dataEvent) {
        if (dataEvent.obj != null) {
            if (dataEvent.obj instanceof CrimeDetailsModel) {
//                listener.onFragmentInteraction(new CrimeAlert());
            }
        }
    }


    // endregion

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Fragment fragment);
    }
}





