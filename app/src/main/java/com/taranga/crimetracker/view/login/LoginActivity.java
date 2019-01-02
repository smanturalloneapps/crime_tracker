package com.taranga.crimetracker.view.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jakewharton.rxbinding.view.RxView;
import com.taranga.crimetracker.R;
import com.taranga.crimetracker.helper.SharedPrefConstants;
import com.taranga.crimetracker.local_databse.LocalDatabaseManager;
import com.taranga.crimetracker.services.TrackerService;
import com.taranga.crimetracker.view.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private CoordinatorLayout loginCoordinatorLayout;
    private TextInputEditText editUserName, editPassword;
    private ProgressDialog progressDialog;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpViews();
    }

    private void setUpViews() {
        loginCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.login_coordinator_layout);
        editUserName = (TextInputEditText) findViewById(R.id.edit_userName);
        editPassword = (TextInputEditText) findViewById(R.id.edit_password);
        btnLogin = (Button) findViewById(R.id.button_login);

        progressDialog = new ProgressDialog(LoginActivity.this);

        checkPlayServices();

        RxView.clicks(btnLogin).subscribe(aVoid -> {
            if (validateFields()) {
                if (LocalDatabaseManager.checkLoginValidation(editUserName.getText().toString().trim(), editPassword.getText().toString().trim())) {

                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Login validating..please wait.");
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                            if (checkPlayServices()) {
                                Intent intentTracking = new Intent(getApplicationContext(), TrackerService.class);
                                getApplicationContext().startService(intentTracking);
                            }

                            SharedPrefConstants.saveLoginStatus(LoginActivity.this, true);
                            SharedPrefConstants.saveUserName(LoginActivity.this, editUserName.getText().toString().trim());
                            // This method will be executed once the timer is over
                            // Start your app main activity
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            // close this activity
                            finish();
                        }
                    }, SPLASH_TIME_OUT);

                } else {
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Login validating..please wait.");
                    progressDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Please check username and password", Toast.LENGTH_SHORT).show();
                        }
                    }, SPLASH_TIME_OUT);
                }
            }
        });
    }

    private boolean validateFields() {
        if (editUserName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return false;
        } else if (editPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

}
