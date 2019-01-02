package com.taranga.crimetracker.view.settings;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.taranga.crimetracker.R;
import com.taranga.crimetracker.helper.SharedPrefConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {

    private View rootView;

    private RadioGroup distanceRadioGroup, mapZoomRadioGroup;

    public Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViews();
    }

    private void setUpViews() {

        distanceRadioGroup = (RadioGroup) rootView.findViewById(R.id.distance_radio_group);
        mapZoomRadioGroup = (RadioGroup) rootView.findViewById(R.id.map_zoom_radio_group);

        double distance = Double.valueOf(SharedPrefConstants.getDistanceForSearching(getContext()));

        if (distance == 0.1) {
            distanceRadioGroup.check(R.id.hundred_meter);
        } else if (distance == 0.2) {
            distanceRadioGroup.check(R.id.two_hundred_meter);
        } else if (distance == 0.5) {
            distanceRadioGroup.check(R.id.five_hundred_meter);
        } else if (distance == 1.0) {
            distanceRadioGroup.check(R.id.thousand_meter);
        }

        int zoomValue = SharedPrefConstants.getMapZoomLevel(getContext());
        if (zoomValue == 15) {
            mapZoomRadioGroup.check(R.id.fifteen_level);
        } else if (zoomValue == 16) {
            mapZoomRadioGroup.check(R.id.sixteen_level);
        } else if (zoomValue == 17) {
            mapZoomRadioGroup.check(R.id.seventeen_level);
        } else if (zoomValue == 18) {
            mapZoomRadioGroup.check(R.id.eighteen_level);
        }

        distanceRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.hundred_meter) {
                SharedPrefConstants.saveMaxDistanceForCrimeLocationSearching(getContext(), "0.1");
//                Toast.makeText(getContext(), "100 m", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.two_hundred_meter) {
                SharedPrefConstants.saveMaxDistanceForCrimeLocationSearching(getContext(), "0.2");
//                Toast.makeText(getContext(), "200 m", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.five_hundred_meter) {
                SharedPrefConstants.saveMaxDistanceForCrimeLocationSearching(getContext(), "0.5");
//                Toast.makeText(getContext(), "500 m", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.thousand_meter) {
                SharedPrefConstants.saveMaxDistanceForCrimeLocationSearching(getContext(), "1.0");
//                Toast.makeText(getContext(), "1000 m", Toast.LENGTH_SHORT).show();
            }
        });


        mapZoomRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.fifteen_level) {
//                Toast.makeText(getContext(), "15", Toast.LENGTH_SHORT).show();
                SharedPrefConstants.saveMapZoomLevel(getContext(), 15);
            } else if (checkedId == R.id.sixteen_level) {
//                Toast.makeText(getContext(), "16", Toast.LENGTH_SHORT).show();
                SharedPrefConstants.saveMapZoomLevel(getContext(), 16);
            } else if (checkedId == R.id.seventeen_level) {
//                Toast.makeText(getContext(), "17", Toast.LENGTH_SHORT).show();
                SharedPrefConstants.saveMapZoomLevel(getContext(), 17);
            } else if (checkedId == R.id.eighteen_level) {
//                Toast.makeText(getContext(), "28", Toast.LENGTH_SHORT).show();
                SharedPrefConstants.saveMapZoomLevel(getContext(), 18);
            }
        });
    }
}
