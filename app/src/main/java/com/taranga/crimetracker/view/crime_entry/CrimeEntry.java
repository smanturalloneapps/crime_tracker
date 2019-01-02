package com.taranga.crimetracker.view.crime_entry;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding.view.RxView;
import com.taranga.crimetracker.R;
import com.taranga.crimetracker.helper.Utils;
import com.taranga.crimetracker.model.CrimeModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import rx.functions.Action1;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeEntry extends Fragment implements ValueEventListener {

    private View rootView;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private DatabaseReference childReference = databaseReference.child("crimedetails");


    private TextInputEditText editDate, editCrime, editTime, editLatitude, editLongitude, editAddress;

    private TextInputLayout crimeInputLayout;

    private ProgressDialog progressDialog;

    private Button btnSubmit, btnClear;
    private Spinner spnCrime;
    private ArrayAdapter<String> crimeArrayAdapter;
    private ArrayList<String> crimeTitleList = new ArrayList<>();

    private final Calendar calendar = Calendar.getInstance();
    // Get the current hour and minute
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minutes = calendar.get(Calendar.MINUTE);

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (calendar != null) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateView(true);
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour = hourOfDay;
            minutes = minute;
            updateTime(hour, minutes);
        }
    };

    public CrimeEntry() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_crime_entry, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViews();
    }

    private void setUpViews() {
        progressDialog = new ProgressDialog(getContext());
        editDate = (TextInputEditText) rootView.findViewById(R.id.edit_date);
        editTime = (TextInputEditText) rootView.findViewById(R.id.edit_time);
        editLatitude = (TextInputEditText) rootView.findViewById(R.id.edit_latitude);
        editLongitude = (TextInputEditText) rootView.findViewById(R.id.edit_longitude);
        editAddress = (TextInputEditText) rootView.findViewById(R.id.edit_address);
        editCrime = (TextInputEditText) rootView.findViewById(R.id.edit_crime);

        crimeInputLayout = (TextInputLayout) rootView.findViewById(R.id.edit_crime_option_layout);

        spnCrime = (Spinner) rootView.findViewById(R.id.spinner_crime);

        btnSubmit = (Button) rootView.findViewById(R.id.button_save);
        btnClear = (Button) rootView.findViewById(R.id.button_clear);

        editDate.setKeyListener(null);
        editTime.setKeyListener(null);

        crimeInputLayout.setVisibility(View.GONE);

        crimeTitleList = Utils.getCrimeTitles();


        updateDateView(false);
        updateTime(hour, minutes);

        crimeArrayAdapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, crimeTitleList);
        crimeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCrime.setAdapter(crimeArrayAdapter);
        crimeArrayAdapter.notifyDataSetChanged();

        RxView.touches(editDate).subscribe(new Action1<MotionEvent>() {
            @Override
            public void call(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    showDatePicker(getContext(), date, calendar).show();
            }
        });

        RxView.touches(editTime).subscribe(new Action1<MotionEvent>() {
            @Override
            public void call(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    new TimePickerDialog(getContext(), timePickerListener, hour, minutes, false).show();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
            }
        });

        spnCrime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item.equals("Other")) {
                    crimeInputLayout.setVisibility(View.VISIBLE);
                } else {
                    crimeInputLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {

                    if (spnCrime.getSelectedItem().toString().equals("Other")) {
                        CrimeModel crimeDetailsModel = new CrimeModel();
                        crimeDetailsModel.setCrime(editCrime.getText().toString().trim());
                        crimeDetailsModel.setDate(editDate.getText().toString().trim());
                        crimeDetailsModel.setTime(editTime.getText().toString().trim());
                        crimeDetailsModel.setLatitude(Double.parseDouble(editLatitude.getText().toString().trim()));
                        crimeDetailsModel.setLongitude(Double.parseDouble(editLongitude.getText().toString().trim()));
                        crimeDetailsModel.setAddress(editAddress.getText().toString().trim());
                        Log.d(TAG, "onClick: model final other: " + crimeDetailsModel);

                        String key = childReference.push().getKey();

                        new PostToFirebaseAsync(key, childReference).execute(crimeDetailsModel);

                    } else {
                        CrimeModel crimeDetailsModel = new CrimeModel();
                        crimeDetailsModel.setCrime(spnCrime.getSelectedItem().toString());
                        crimeDetailsModel.setDate(editDate.getText().toString().trim());
                        crimeDetailsModel.setTime(editTime.getText().toString().trim());
                        crimeDetailsModel.setLatitude(Double.parseDouble(editLatitude.getText().toString().trim()));
                        crimeDetailsModel.setLongitude(Double.parseDouble(editLongitude.getText().toString().trim()));
                        crimeDetailsModel.setAddress(editAddress.getText().toString().trim());
                        Log.d(TAG, "onClick: model final option: " + crimeDetailsModel);
                        String key = childReference.push().getKey();
                        new PostToFirebaseAsync(key, childReference).execute(crimeDetailsModel);
                    }
                }
            }
        });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private class PostToFirebaseAsync extends AsyncTask<CrimeModel, String, String> {
        private boolean isSaved;
        private String key;
        private DatabaseReference childReference;

        public PostToFirebaseAsync(String key, DatabaseReference childReference) {
            this.key = key;
            this.childReference = childReference;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress("Saving please wait..");
        }

        @Override
        protected String doInBackground(CrimeModel... crimeDetailsModels) {
            childReference.child(key).setValue(crimeDetailsModels[0], new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                hideProgress();
                                Toast.makeText(getContext(), "Data not saved.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                hideProgress();
                                Toast.makeText(getContext(), "Data Saved successfuly", Toast.LENGTH_SHORT).show();
                                clearData();
                            }
                        });
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
        }
    }

    private void clearData() {
        updateDateView(false);
        Calendar calendar = Calendar.getInstance();
        // Get the current hour and minute
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        updateTime(hour, minutes);
        spnCrime.setSelection(0);
        editLatitude.setText(null);
        editLongitude.setText(null);
        editAddress.setText(null);
    }

    private boolean validate() {
        if (editDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Crime date is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (editTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Crime time is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (spnCrime.getSelectedItem().toString().equals("Select Crime")) {
            Toast.makeText(getContext(), "Please select crime option", Toast.LENGTH_SHORT).show();
            return false;
        } else if (spnCrime.getSelectedItem().toString().equals("Other") && editCrime.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Please enter crime option", Toast.LENGTH_SHORT).show();
            return false;
        } else if (editLatitude.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Crime place latitude is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (editLongitude.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Crime place longitude is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (editAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Crime address is required", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void updateDateView(boolean isOnClick) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (isOnClick) {
            editDate.setText(sdf.format(calendar.getTime()));
        } else {
            editDate.setText(sdf.format(new Date()));
        }
    }

    private DatePickerDialog showDatePicker(Context context, DatePickerDialog.OnDateSetListener listner, Calendar newCalender) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(rootView.getContext(), listner, newCalender
                .get(Calendar.YEAR), newCalender.get(Calendar.MONTH),
                newCalender.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        return datePickerDialog;
    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    private void updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        editTime.setText(aTime);
    }


    @Override
    public void onStart() {
        super.onStart();
        childReference.addValueEventListener(this);
    }

    private void showProgress(String message) {
        if (progressDialog != null) {
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    private void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}



























/* private void postCrimeDataToServer(Context context, CrimeDetailsModel crimeDetailsModel) {
        RestPostAPI restPostAPI = new RestPostClient(context).getInstance(context);
        Observable<String> observable = restPostAPI.postCrimeDetailsToRx(crimeDetailsModel);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        // cast to retrofit.HttpException to get the response code
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            int code = response.code();
                            e.printStackTrace();
                            Log.e(TAG, "onUpdateError. Code: " + code + ". ", e.getCause());
                        }
                    }

                    @Override
                    public void onNext(String s) {

                    }

                });
    }
*/
