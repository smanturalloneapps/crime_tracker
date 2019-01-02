package com.taranga.crimetracker.view.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.jakewharton.rxbinding.view.RxView;
import com.taranga.crimetracker.R;
import com.taranga.crimetracker.helper.AppConstants;
import com.taranga.crimetracker.helper.SharedPrefConstants;
import com.taranga.crimetracker.model.CrimeDetailsModel;
import com.taranga.crimetracker.rest.clients.RestGetClient;
import com.taranga.crimetracker.rest.services.RestGetAPI;
import com.taranga.crimetracker.rest.services.RestService;
import com.taranga.crimetracker.view.crime_alert.CrimeAlert;
import com.taranga.crimetracker.view.crime_entry.CrimeEntry;
import com.taranga.crimetracker.view.crime_history.CrimeHistory;
import com.taranga.crimetracker.view.login.LoginActivity;
import com.taranga.crimetracker.view.settings.Settings;

import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private TextView textTitle, textLogout;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();

        if (getIntent().getBooleanExtra(AppConstants.NEW_LOCATION_DETAILS, false)) {
            getIntent().removeExtra(AppConstants.NEW_LOCATION_DETAILS);

            fragmentManager.beginTransaction().replace(R.id.main_frame, new MainFragment(), Integer.toString(getFragmentCount())).commit();
            finish();
        } else {
            fragmentManager.beginTransaction().replace(R.id.main_frame, new MainFragment(), Integer.toString(getFragmentCount())).commit();
        }
//        fragmentTransaction.replace(R.id.main_frame, new MainFragment()).commit();

        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(getResources().getString(R.string.app_name));

        /*if (checkPlayServices()) {
            Intent intentTracking = new Intent(getApplicationContext(), TrackerService.class);
            getApplicationContext().startService(intentTracking);
        }*/
        setUpViews();
    }

    private void setUpViews() {

        textLogout = (TextView) findViewById(R.id.text_logout);

        String userName = SharedPrefConstants.getUserName(MainActivity.this);

        if (userName != null) {
            String logout = "Logout   " + "(" + userName + ")";
            textLogout.setText(logout);
        }

        String distance = SharedPrefConstants.getDistanceForSearching(getApplicationContext());

        Log.d(TAG, "setUpViews: distance: " + distance);

        if (distance.isEmpty()) {
            Log.d(TAG, "setUpViews: distance save 500m");
            SharedPrefConstants.saveMaxDistanceForCrimeLocationSearching(getApplicationContext(), "0.5");
        }

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);

        RxView.clicks(textLogout).subscribe(aVoid -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Are you sure to Logout ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) ->
                            logoutCrimeTracker())
                    .setNegativeButton("No", (dialog, i) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public void onBackPressed() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_frame);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragment instanceof MainFragment) {
            super.onBackPressed();
        } else if (getFragmentCount() == 0) {
            fragmentManager.popBackStack();
            fragmentTransaction.replace(R.id.main_frame, new MainFragment()).commit();
            textTitle.setText("Crime Tracker");
        } else {
            super.onBackPressed();
        }
    }

    protected int getFragmentCount() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

      /*  //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_crime_alert) {
            fragmentTransaction.replace(R.id.main_frame, new CrimeAlert(), Integer.toString(getFragmentCount())).commit();
            textTitle.setText("Crime Alert");
        } else if (id == R.id.nav_settings) {
            fragmentTransaction.replace(R.id.main_frame, new Settings(), Integer.toString(getFragmentCount())).commit();
            textTitle.setText("Settings");
        } else if (id == R.id.nav_crime_entry) {
            fragmentTransaction.replace(R.id.main_frame, new CrimeEntry(), Integer.toString(getFragmentCount())).commit();
            textTitle.setText("Crime Entry");
        } else if (id == R.id.nav_crime_history) {
            fragmentTransaction.replace(R.id.main_frame, new CrimeHistory(), Integer.toString(getFragmentCount())).commit();
            textTitle.setText("Crime History");
        } else if (id == R.id.nav_share_location) {

            // Use package name which we want to check
            boolean isAppInstalled = appInstalledOrNot("com.whatsapp");
            if (isAppInstalled) {
                showAlertToShareLocation();
                fragmentTransaction.replace(R.id.main_frame, new MainFragment(), Integer.toString(getFragmentCount())).commit();
                textTitle.setText("Crime Tracker");
            } else {
                Toast.makeText(this, "Whatsapp is not installed. Installed it and try again.", Toast.LENGTH_LONG).show();
                fragmentTransaction.replace(R.id.main_frame, new MainFragment(), Integer.toString(getFragmentCount())).commit();
                textTitle.setText("Crime Tracker");
            }

        } else if (id == R.id.nav_sync) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Message")
                    .setMessage("Are you sure to sync data.")
                    .setCancelable(false)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getDetailsFromServer(getApplicationContext());
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAlertToShareLocation() {
        LatLng latLng = SharedPrefConstants.getCurrentLatitudeAndLongitude(MainActivity.this);
        if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
            String whatsAppMessage = "Location Shared From Crime Tracker.\n\n" + "This is my current location. \n" + "http://maps.google.com/maps?saddr=" + latLng.latitude + "," + latLng.longitude + " \n"
                    + "My Location Map Snapshot \n" + "https://maps.googleapis.com/maps/api/staticmap?zoom=17&size=600x600&sensor=false&maptype=roadmap&markers=color:red|" + latLng.latitude + "," + latLng.longitude;
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } else {
            Toast.makeText(this, "Location not available, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if (Utils.isNetworkAvailable(getApplicationContext())) {
            getDetailsFromServer(getApplicationContext());
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getDetailsFromServer(Context context) {

        progressDialog.setMessage("Syncing..Please wait.");
        progressDialog.show();

        RestGetAPI restGetAPI = new RestGetClient(context).getInstance(context);
        Observable<RestService> observableRx = restGetAPI.getCrimeDetailsFromRx();
        observableRx.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Subscriber<RestService>() {
                    @Override
                    public void onCompleted() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            int code = response.code();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(RestService restService) {
                        progressDialog.dismiss();
                        if (restService != null) {
                            final List<CrimeDetailsModel> crimeDetailsModelList = restService.getCrimeDetailsSheet();

                            if (crimeDetailsModelList.size() > 0) {
                                Log.d(TAG, "onNext: size: " + crimeDetailsModelList.size());

                              /*  runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MainActivity.this,"Successfully synced " + crimeDetailsModelList.size() + " entries", Toast.LENGTH_SHORT).show();
                                    }
                                });*/

//                                totalSynced("Successfully synced " + crimeDetailsModelList.size() + " entries");

//                                Toast.makeText(MainActivity.this, "Successfully synced " + crimeDetailsModelList.size() + " entries", Toast.LENGTH_SHORT).show();

                                CrimeDetailsModel detailsModelItems = new CrimeDetailsModel();
                                detailsModelItems.delete();
                                for (CrimeDetailsModel detailsModelItem : crimeDetailsModelList) {
                                    boolean isSaved = detailsModelItem.save();
                                }
                            } else {
//                                Toast.makeText(MainActivity.this, "Failed to Sync", Toast.LENGTH_SHORT).show();
//                                totalSynced("Failed to Sync");
                              /*  runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MainActivity.this,"Failed to Sync " + crimeDetailsModelList.size() + " entries", Toast.LENGTH_SHORT).show();
                                    }
                                });*/
                            }
                        }
                    }
                });
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it doesn't, display a
     * dialog that allows users to download the APK from the Google Play Store or enable it in the
     * device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 111)
                        .show();
            } else {
                Log.d(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void logoutCrimeTracker() {
        SharedPrefConstants.removeLoginStatus(MainActivity.this);
        SharedPrefConstants.removeUserName(MainActivity.this);
        Toast.makeText(MainActivity.this, "Successfully Logout", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}