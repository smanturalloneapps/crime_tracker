package com.taranga.crimetracker.view.crime_alert;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.taranga.crimetracker.R;
import com.taranga.crimetracker.helper.AppConstants;
import com.taranga.crimetracker.helper.SharedPrefConstants;
import com.taranga.crimetracker.local_databse.LocalDatabaseManager;
import com.taranga.crimetracker.model.CrimeDetailsModel;
import com.taranga.crimetracker.model.event_bus.DataEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeAlert extends Fragment {

    private static final String TAG = CrimeAlert.class.getSimpleName();
    private View rootView, dividerOne, dividerTwo;

    private CardView mainCardView;

    private TextView textNoLocations, textNearestLocation, textCrime, textDate, textTime, textAddress;

    public CrimeAlert() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_crime_alert, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainCardView = (CardView) rootView.findViewById(R.id.main_card_view);
        dividerOne = (View) rootView.findViewById(R.id.divider_0);
        dividerTwo = (View) rootView.findViewById(R.id.divider_1);

        textNoLocations = (TextView) rootView.findViewById(R.id.no_places);
        textNearestLocation = (TextView) rootView.findViewById(R.id.text_crime_places_title);
        textCrime = (TextView) rootView.findViewById(R.id.text_crime_detail);
        textDate = (TextView) rootView.findViewById(R.id.text_crime_date);
        textTime = (TextView) rootView.findViewById(R.id.text_crime_time);
        textAddress = (TextView) rootView.findViewById(R.id.text_crime_address);

        LatLng currentLatLng = SharedPrefConstants.getCurrentLatitudeAndLongitude(getContext());
        double latitude = currentLatLng.latitude;
        double longitude = currentLatLng.longitude;

        textNoLocations.setVisibility(View.GONE);

        textNearestLocation.setVisibility(View.GONE);
        mainCardView.setVisibility(View.GONE);
        dividerOne.setVisibility(View.GONE);
        dividerTwo.setVisibility(View.GONE);

        if (!(latitude == 0.0) && !(longitude == 0.0)) {
            CrimeDetailsModel crimeDetailsModel = LocalDatabaseManager.getNearestCrimeSpotLocation(getContext(), new LatLng(latitude, longitude));

            Log.d(TAG, "onActivityCreated: nearestModel: " + crimeDetailsModel.toString());

            if (crimeDetailsModel.getCrime() != null) {
                textNoLocations.setVisibility(View.GONE);
                textNearestLocation.setVisibility(View.VISIBLE);
                mainCardView.setVisibility(View.VISIBLE);
                dividerOne.setVisibility(View.VISIBLE);
                dividerTwo.setVisibility(View.VISIBLE);

                textCrime.setText(crimeDetailsModel.getCrime());
                textDate.setText(crimeDetailsModel.getDate());
                textTime.setText(crimeDetailsModel.getTime());
                textAddress.setText(crimeDetailsModel.getAddress());

            } else {
                textNoLocations.setVisibility(View.VISIBLE);

                textNearestLocation.setVisibility(View.GONE);
                mainCardView.setVisibility(View.GONE);
                dividerOne.setVisibility(View.GONE);
                dividerTwo.setVisibility(View.GONE);
            }
        }else{
            textNoLocations.setVisibility(View.VISIBLE);

            textNearestLocation.setVisibility(View.GONE);
            mainCardView.setVisibility(View.GONE);
            dividerOne.setVisibility(View.GONE);
            dividerTwo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        SharedPrefConstants.saveCrimeAlertFragmentStatus(getContext(), true);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPrefConstants.saveCrimeAlertFragmentStatus(getContext(), true);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPrefConstants.saveCrimeAlertFragmentStatus(getContext(), false);
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPrefConstants.saveCrimeAlertFragmentStatus(getContext(), false);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPrefConstants.saveCrimeAlertFragmentStatus(getContext(), false);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    // This method will be called when a ResultEvent is posted.
    @SuppressWarnings(AppConstants.UNUSED)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(DataEvent dataEvent) {
        if (dataEvent.obj != null) {
            if (dataEvent.obj instanceof CrimeDetailsModel) {
//                listener.onFragmentInteraction(new CrimeAlert());
                if (SharedPrefConstants.getCrimeAlertFragmentStatus(getContext())) {
                    //new line added
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(this).attach(this).commit();
                }
            }
        }
    }
}
